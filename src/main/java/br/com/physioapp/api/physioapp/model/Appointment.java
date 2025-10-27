package br.com.physioapp.api.physioapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_datetime", columnList = "date_time"),
        @Index(name = "idx_appointment_patient", columnList = "patient_id"),
        @Index(name = "idx_appointment_physio", columnList = "physiotherapist_id"),
        @Index(name = "idx_appointment_physio_datetime", columnList = "physiotherapist_id,date_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "physiotherapist_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_physio"))
    private Physiotherapist physiotherapist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false, foreignKey = @ForeignKey(name = "fk_appointment_patient"))
    private Patient patient;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AppointmentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}

