package br.com.physioapp.api.physioapp.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.physioapp.api.physioapp.model.Patient;
import jakarta.persistence.LockModeType;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

  Page<Patient> findByFullnameContainingIgnoreCase(String name, Pageable pageable);

  interface Summary {
    UUID getId();

    String getFullname();

    String getEmail();
  }

  Page<Summary> findAllProjectedByFullnameContainingIgnoreCase(String name, Pageable pageable);

  boolean existsByEmail(String email);

  @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.notifications WHERE p.id = :id")
  Optional<Patient> findByIdWithNotifications(@Param("id") UUID id);

  @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.appointments WHERE p.id = :id")
  Optional<Patient> findByIdWithAppointments(@Param("id") UUID id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Patient p WHERE p.id = :id")
  Optional<Patient> lockById(@Param("id") UUID id);

  @Modifying
  @Query("DELETE FROM Patient p WHERE p.id IN :ids")
  int deleteAllByIds(@Param("ids") Collection<UUID> ids);

  Optional<Patient> findByEmail(String email);
}
