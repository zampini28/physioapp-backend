package br.com.physioapp.api.physioapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.AppointmentRequestDTO;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.exception.SchedulingConflictException;
import br.com.physioapp.api.physioapp.model.Appointment;
import br.com.physioapp.api.physioapp.model.AppointmentStatus;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.repository.AppointmentRepository;
import br.com.physioapp.api.physioapp.repository.PatientRepository;
import br.com.physioapp.api.physioapp.repository.PhysiotherapistRepository;

@Service
public class AppointmentService {

  private final int defaultPageSize = 50;

  @Autowired
  private AppointmentRepository appointmentRepository;

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private PhysiotherapistRepository physiotherapistRepository;

  @Transactional
  public Appointment createAppointment(AppointmentRequestDTO request) {
    Patient patient = patientRepository.findById(request.patientId())
        .orElseThrow(() -> new ResourceNotFoundException("ID Paciente não encontrado: " + request.patientId()));

    Physiotherapist physiotherapist = physiotherapistRepository.findById(request.physiotherapistId())
        .orElseThrow(
            () -> new ResourceNotFoundException("ID Fisioterapeuta não encontrado: " + request.physiotherapistId()));

    checkSchedulingConflict(physiotherapist.getId(), request.dateTime(), request.durationMinutes(), null);

    Appointment newAppointment = Appointment.builder()
        .patient(patient)
        .physiotherapist(physiotherapist)
        .dateTime(request.dateTime())
        .durationMinutes(request.durationMinutes())
        .notes(request.notes())
        .status(AppointmentStatus.SCHEDULED)
        .build();

    return appointmentRepository.save(newAppointment);
  }

  @Transactional(readOnly = true)
  public Appointment getAppointmentById(UUID id) {
    return appointmentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID Consulta não encontrada: " + id));
  }

  @Transactional(readOnly = true)
  public List<Appointment> getAppointmentsForPatient(UUID patientId) {
    if (!patientRepository.existsById(patientId)) {
      throw new ResourceNotFoundException("ID Paciente não encontrado: " + patientId);
    }
    return appointmentRepository.findByPatientIdOrderByDateTime(patientId);
  }

  @Transactional(readOnly = true)
  public List<Appointment> getAppointments(UUID patientId, UUID physiotherapistId) {
    if (patientId != null && physiotherapistId != null) {
      return appointmentRepository.findByPatientIdAndPhysiotherapistIdOrderByDateTime(
          patientId, physiotherapistId);
    } else if (patientId != null) {
      return appointmentRepository.findByPatientIdOrderByDateTime(patientId);
    } else if (physiotherapistId != null) {
      return appointmentRepository.findByPhysiotherapistIdOrderByDateTime(physiotherapistId);
    } else {
      Pageable limit = PageRequest.of(0, defaultPageSize);
      return appointmentRepository.findAll(limit).getContent();
    }
  }

  @Transactional
  public Appointment cancelAppointment(UUID id) {
    Appointment appointment = getAppointmentById(id);

    if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
      throw new IllegalStateException("Não é possível cancelar um compromisso concluído.");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);
    return appointmentRepository.save(appointment);
  }

  private void checkSchedulingConflict(UUID physioId, LocalDateTime start, int duration, UUID appointmentIdToExclude) {
    LocalDateTime end = start.plusMinutes(duration);
    List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(physioId, start, end,
        appointmentIdToExclude);
    if (!overlapping.isEmpty()) {
      throw new SchedulingConflictException(
          "O fisioterapeuta não está disponível no horário solicitado. Conflito com o ID do agendamento: "
              + overlapping.get(0).getId());
    }
  }
}