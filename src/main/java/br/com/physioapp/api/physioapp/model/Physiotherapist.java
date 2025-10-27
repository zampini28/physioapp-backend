package br.com.physioapp.api.physioapp.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@DiscriminatorValue("PHYSIO")
public class Physiotherapist extends User {

    @Column(name = "crefito", unique = true)
    private String crefito;

    @OneToMany(mappedBy = "physiotherapist", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Appointment> appointments;

    public List<Appointment> getAppointmentsUnmodifiable() {
        return Collections.unmodifiableList(appointments);
    }
}
