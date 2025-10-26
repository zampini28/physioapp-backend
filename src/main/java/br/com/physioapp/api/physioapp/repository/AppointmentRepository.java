package br.com.physioapp.api.physioapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.physioapp.api.physioapp.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

  List<Appointment> findByPatientIdOrderByDateTime(UUID patientId);

  List<Appointment> findByPhysiotherapistIdOrderByDateTime(UUID physiotherapistId);

  List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

  @Query(value = "SELECT * FROM appointments a WHERE a.physiotherapist_id = :physioId " +
      "AND a.status <> 'CANCELLED' " +
      "AND a.id <> :excludeAppointmentId " +
      "AND a.date_time < :end " +
      "AND (a.date_time + (a.duration_minutes * INTERVAL '1 minute')) > :start", nativeQuery = true)
  List<Appointment> findOverlappingAppointments(
      @Param("physioId") UUID physioId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("excludeAppointmentId") UUID appointmentIdToExclude);

}
