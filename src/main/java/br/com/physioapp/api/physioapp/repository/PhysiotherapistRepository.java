package br.com.physioapp.api.physioapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.physioapp.api.physioapp.model.Physiotherapist;

@Repository
public interface PhysiotherapistRepository extends JpaRepository<Physiotherapist, Long> {
    Optional<Physiotherapist> findByCrefito(String crefito);
}