package br.com.physioapp.api.physioapp.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@DiscriminatorValue("PATIENT")
public class Patient extends User {

    @OneToMany(mappedBy = "patient", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "recipient", cascade = {
        CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notification> notifications;

    public List<Appointment> getAppointmentsUnmodifiable() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Notification> getNotificationsUnmodifiable() {
        return Collections.unmodifiableList(notifications);
    }
}