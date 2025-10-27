package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.physioapp.api.physioapp.model.AppointmentStatus;

public record AppointmentResponseDTO(
  UUID id,
  UUID physiotherapistId,
  String physiotherapistName,
  UUID patientId,
  String patientName,
  LocalDateTime dateTime,
  Integer durationMinutes,
  AppointmentStatus status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
