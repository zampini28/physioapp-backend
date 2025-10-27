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

import br.com.physioapp.api.physioapp.model.Physiotherapist;
import jakarta.persistence.LockModeType;

public interface PhysiotherapistRepository extends JpaRepository<Physiotherapist, UUID> {

  Optional<Physiotherapist> findByCrefito(String crefito);

  boolean existsByCrefito(String crefito);

  @Query("SELECT p FROM Physiotherapist p LEFT JOIN FETCH p.appointments WHERE p.id = :id")
  Optional<Physiotherapist> findByIdWithAppointments(UUID id);

  interface Summary {
    UUID getId();

    String getFullname();

    String getEmail();

    String getCrefito();
  }

  Page<Summary> findAllProjectedBy(Pageable pageable);

  @Query("SELECT p FROM Physiotherapist p WHERE LOWER(p.crefito) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.fullname) LIKE LOWER(CONCAT('%', :term, '%'))")
  Page<Physiotherapist> searchByCrefitoOrName(@Param("term") String term, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Physiotherapist p WHERE p.id = :id")
  Optional<Physiotherapist> lockById(UUID id);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE Physiotherapist p SET p.crefito = :crefito WHERE p.id = :id")
  int updateCrefitoById(@Param("id") UUID id, @Param("crefito") String crefito);

  @Modifying
  @Query("DELETE FROM Physiotherapist p WHERE p.id IN :ids")
  int deleteAllByIds(@Param("ids") Collection<UUID> ids);
}
