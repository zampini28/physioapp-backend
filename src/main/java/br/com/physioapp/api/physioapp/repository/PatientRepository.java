package br.com.physioapp.api.physioapp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.physioapp.api.physioapp.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
  List<Patient> findByFullnameContainingIgnoreCase(String name);
}
