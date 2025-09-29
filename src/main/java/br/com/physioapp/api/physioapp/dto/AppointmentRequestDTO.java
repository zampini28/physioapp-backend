package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;

public record AppointmentRequestDTO(
    Long physiotherapistId,
    Long patientId, 
    LocalDateTime dateTime,
    Integer durationMinutes,
    String notes
) {}
