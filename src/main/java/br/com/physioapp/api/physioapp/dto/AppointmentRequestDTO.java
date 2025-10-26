package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(
    UUID physiotherapistId,
    UUID patientId,
    LocalDateTime dateTime,
    Integer durationMinutes,    
    String notes) {
}
