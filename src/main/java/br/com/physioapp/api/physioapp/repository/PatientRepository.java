package br.com.physioapp.api.physioapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.physioapp.api.physioapp.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByFullnameContainingIgnoreCase(String name);
}