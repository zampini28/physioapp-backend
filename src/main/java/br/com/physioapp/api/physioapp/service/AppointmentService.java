package br.com.physioapp.api.physioapp.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.AppointmentRequestDTO;
import br.com.physioapp.api.physioapp.dto.AppointmentResponseDTO;
import br.com.physioapp.api.physioapp.events.AppointmentCancelledEvent;
import br.com.physioapp.api.physioapp.events.AppointmentCreatedEvent;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.exception.SchedulingConflictException;
import br.com.physioapp.api.physioapp.mapper.AppointmentMapper;
import br.com.physioapp.api.physioapp.model.Appointment;
import br.com.physioapp.api.physioapp.model.AppointmentStatus;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.repository.AppointmentRepository;
import br.com.physioapp.api.physioapp.repository.PatientRepository;
import br.com.physioapp.api.physioapp.repository.PhysiotherapistRepository;

@Service
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final PatientRepository patientRepository;
  private final PhysiotherapistRepository physiotherapistRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final AppointmentMapper mapper;
  private final Clock clock;
  private final int defaultPageSize = 50;

  public AppointmentService(AppointmentRepository appointmentRepository,
      PatientRepository patientRepository,
      PhysiotherapistRepository physiotherapistRepository,
      ApplicationEventPublisher eventPublisher,
      AppointmentMapper mapper,
      Clock clock) {
    this.appointmentRepository = appointmentRepository;
    this.patientRepository = patientRepository;
    this.physiotherapistRepository = physiotherapistRepository;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
    this.clock = clock;
  }

  @Transactional
  public AppointmentResponseDTO createAppointment(AppointmentRequestDTO request) {
    validateRequest(request);

    Patient patient = patientRepository.findById(request.patientId())
        .orElseThrow(() -> new ResourceNotFoundException("ID Paciente não encontrado: " + request.patientId()));

    Physiotherapist physio = physiotherapistRepository.findById(request.physiotherapistId())
        .orElseThrow(
            () -> new ResourceNotFoundException("ID Fisioterapeuta não encontrado: " + request.physiotherapistId()));

    LocalDateTime start = request.dateTime();
    LocalDateTime end = start.plusMinutes(request.durationMinutes());

    if (hasConflict(physio.getId(), start, end, null)) {
      throw new SchedulingConflictException("Conflito de agendamento detectado para o fisioterapeuta");
    }

    Appointment ap = Appointment.builder()
        .patient(patient)
        .physiotherapist(physio)
        .dateTime(start)
        .durationMinutes(request.durationMinutes())
        .notes(request.notes())
        .status(AppointmentStatus.SCHEDULED)
        .build();

    try {
      Appointment saved = appointmentRepository.save(ap);
      AppointmentResponseDTO dto = mapper.toResponse(saved);
      eventPublisher.publishEvent(new AppointmentCreatedEvent(dto));
      return dto;
    } catch (DataIntegrityViolationException ex) {
      lockPhysioRangeAndCheckThenSave(physio.getId(), start, end, ap);
      return mapper.toResponse(appointmentRepository.findById(ap.getId()).orElseThrow());
    }
  }

  @Transactional(readOnly = true)
  public AppointmentResponseDTO getAppointmentById(UUID id) {
    Appointment ap = appointmentRepository.findByIdWithParticipants(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID Consulta não encontrada: " + id));
    return mapper.toResponse(ap);
  }

  @Transactional(readOnly = true)
  public Page<AppointmentResponseDTO> getAppointments(UUID patientId, UUID physiotherapistId, int page, int size) {
    Pageable pageable = PageRequest.of(page, Math.min(size, defaultPageSize));
    Page<Appointment> pageResult;

    if (patientId != null && physiotherapistId != null) {
      pageResult = appointmentRepository.findByPatientIdAndPhysiotherapistIdOrderByDateTime(patientId,
          physiotherapistId, pageable);
    } else if (patientId != null) {
      pageResult = appointmentRepository.findByPatientIdOrderByDateTime(patientId, pageable);
    } else if (physiotherapistId != null) {
      pageResult = appointmentRepository.findByPhysiotherapistIdOrderByDateTime(physiotherapistId, pageable);
    } else {
      pageResult = appointmentRepository.findAll(pageable);
    }

    return pageResult.map(mapper::toResponse);
  }

  @Transactional
  public AppointmentResponseDTO cancelAppointment(UUID id) {
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID Consulta não encontrada: " + id));

    if (!appointment.getStatus().canTransitionTo(AppointmentStatus.CANCELLED)) {
      throw new IllegalStateException("Não é possível cancelar o agendamento no estado atual.");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);
    Appointment updated = appointmentRepository.save(appointment);
    AppointmentResponseDTO dto = mapper.toResponse(updated);
    eventPublisher.publishEvent(new AppointmentCancelledEvent(dto));
    return dto;
  }

  @Transactional
  public int cancelAppointmentsBulk(Collection<UUID> ids) {
    return appointmentRepository.updateStatusBulk(ids, AppointmentStatus.CANCELLED);
  }

  private void validateRequest(AppointmentRequestDTO req) {
    if (req.durationMinutes() == null || req.durationMinutes() <= 0 || req.durationMinutes() > 24 * 60) {
      throw new IllegalArgumentException("Duração inválida");
    }
    LocalDateTime now = LocalDateTime.now(clock);
    if (req.dateTime().isBefore(now)) {
      throw new IllegalArgumentException("Data/hora deve ser no futuro");
    }
  }

  private boolean hasConflict(UUID physioId, LocalDateTime start, LocalDateTime end, UUID excludeId) {
    return !appointmentRepository.findOverlappingAppointmentsPostgres(physioId, start, end, excludeId).isEmpty();
  }

  @Transactional
  protected void lockPhysioRangeAndCheckThenSave(UUID physioId, LocalDateTime start, LocalDateTime end,
      Appointment apToSave) {
    appointmentRepository.lockAppointmentsForPhysioInRange(physioId, start, end);
    if (hasConflict(physioId, start, end, null)) {
      throw new SchedulingConflictException("Conflito detectado ao tentar salvar após tentativa concorrente");
    }
    Appointment saved = appointmentRepository.save(apToSave);
    eventPublisher.publishEvent(new AppointmentCreatedEvent(mapper.toResponse(saved)));
  }

  @Transactional(readOnly = true)
  public Page<AppointmentResponseDTO> getAppointments(UUID patientId, UUID physiotherapistId, Pageable pageable) {
    Page<Appointment> pageResult;
    if (patientId != null && physiotherapistId != null) {
      pageResult = appointmentRepository.findByPatientIdAndPhysiotherapistIdOrderByDateTime(
          patientId, physiotherapistId, pageable);
    } else if (patientId != null) {
      pageResult = appointmentRepository.findByPatientIdOrderByDateTime(patientId, pageable);
    } else if (physiotherapistId != null) {
      pageResult = appointmentRepository.findByPhysiotherapistIdOrderByDateTime(physiotherapistId, pageable);
    } else {
      pageResult = appointmentRepository.findAll(pageable);
    }

    return pageResult.map(mapper::toResponse);
  }
}