package br.com.physioapp.api.physioapp.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.physioapp.api.physioapp.model.Appointment;
import br.com.physioapp.api.physioapp.model.AppointmentStatus;
import jakarta.persistence.LockModeType;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByPatientIdOrderByDateTime(UUID patientId, Pageable pageable);

    Page<Appointment> findByPhysiotherapistIdOrderByDateTime(UUID physiotherapistId, Pageable pageable);

    Page<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Appointment> findByPatientIdAndPhysiotherapistIdOrderByDateTime(UUID patientId, UUID physiotherapistId,
            Pageable pageable);

    interface Summary {
        UUID getId();

        LocalDateTime getDateTime();

        Integer getDurationMinutes();

        AppointmentStatus getStatus();

        UUID getPhysiotherapistId();

        UUID getPatientId();
    }

    Page<Summary> findByPhysiotherapistIdOrderByDateTime(UUID physiotherapistId, Pageable pageable,
            Class<Summary> projection);

    @Query("SELECT a FROM Appointment a " +
            "LEFT JOIN FETCH a.patient p " +
            "LEFT JOIN FETCH a.physiotherapist ph " +
            "WHERE a.id = :id")
    Optional<Appointment> findByIdWithParticipants(@Param("id") UUID id);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.physiotherapist.id = :physioId " +
            "  AND a.status <> br.com.physioapp.api.physioapp.model.AppointmentStatus.CANCELLED " +
            "  AND (:excludeId IS NULL OR a.id <> :excludeId) " +
            "  AND a.dateTime < :end " +
            "  AND (a.dateTime + function('timestampadd',  MINUTE, a.durationMinutes, a.dateTime)) > :start")
    List<Appointment> findOverlappingAppointmentsJpa(@Param("physioId") UUID physioId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeId") UUID excludeAppointmentId);

    @Query(value = "SELECT * FROM appointments a WHERE a.physiotherapist_id = :physioId " +
            "AND a.status <> 'CANCELLED' " +
            "AND (:excludeAppointmentId IS NULL OR a.id <> :excludeAppointmentId) " +
            "AND a.date_time < :end " +
            "AND (a.date_time + (a.duration_minutes * INTERVAL '1 minute')) > :start", nativeQuery = true)
    List<Appointment> findOverlappingAppointmentsPostgres(@Param("physioId") UUID physioId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("excludeAppointmentId") UUID appointmentIdToExclude);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Appointment a WHERE a.physiotherapist.id = :physioId AND a.dateTime BETWEEN :start AND :end")
    List<Appointment> lockAppointmentsForPhysioInRange(@Param("physioId") UUID physioId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id IN :ids")
    int updateStatusBulk(@Param("ids") Collection<UUID> ids, @Param("status") AppointmentStatus status);

    long countByPhysiotherapistIdAndStatus(UUID physiotherapistId, AppointmentStatus status);

    long countByPatientIdAndStatus(UUID patientId, AppointmentStatus status);
}
