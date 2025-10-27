package br.com.physioapp.api.physioapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import jakarta.persistence.LockModeType;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<Physiotherapist> findByCrefito(String crefito);

  boolean existsByCrefito(String crefito);

  @Query("SELECT p FROM Physiotherapist p LEFT JOIN FETCH p.appointments WHERE p.id = :id")
  Optional<Physiotherapist> findPhysioWithAppointmentsById(UUID id);

  @Query("SELECT pt FROM Patient pt LEFT JOIN FETCH pt.notifications WHERE pt.id = :id")
  Optional<Patient> findPatientWithNotificationsById(UUID id);

  interface Summary {
    UUID getId();

    String getFullname();

    String getEmail();

    UserType getType();

    String getCrefito();
  }

  Page<Summary> findByType(UserType type, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> lockById(UUID id);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE User u SET u.password = :hash WHERE u.id = :id")
  int updatePassword(UUID id, String hash);

  @Query("SELECT u FROM User u WHERE u.id = :id")
  Optional<User> findByIdWithType(UUID id);

  @Query("SELECT u FROM User u")
  Page<Summary> findAllProjectedBy(Pageable pageable);
}
