package br.com.physioapp.api.physioapp.service;

import java.time.LocalDateTime;
import java.util.List;

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

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PhysiotherapistRepository physiotherapistRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, PhysiotherapistRepository physiotherapistRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.physiotherapistRepository = physiotherapistRepository;
    }

    @Transactional
    public Appointment createAppointment(AppointmentRequestDTO request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("ID Paciente não encontrado: " + request.patientId()));

        Physiotherapist physiotherapist = physiotherapistRepository.findById(request.physiotherapistId())
                .orElseThrow(() -> new ResourceNotFoundException("ID Fisioterapeuta não encontrado: " + request.physiotherapistId()));

        checkSchedulingConflict(physiotherapist.getId(), request.dateTime(), request.durationMinutes(), -1L);

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
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID Consulta não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("ID Paciente não encontrado: " + patientId);
        }
        return appointmentRepository.findByPatientIdOrderByDateTime(patientId);
    }

    @Transactional
    public Appointment cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Não é possível cancelar um compromisso concluído.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    private void checkSchedulingConflict(Long physioId, LocalDateTime start, int duration, Long appointmentIdToExclude) {
        LocalDateTime end = start.plusMinutes(duration);
        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointments(physioId, start, end, appointmentIdToExclude);
        if (!overlapping.isEmpty()) {
            throw new SchedulingConflictException("O fisioterapeuta não está disponível no horário solicitado. Conflito com o ID do agendamento: " + overlapping.get(0).getId());
        }
    }
}