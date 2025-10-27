package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AppointmentRequestDTO(
  @NotNull(message = "ID do fisioterapeuta é obrigatório")
  UUID physiotherapistId,
  
  @NotNull(message = "ID do paciente é obrigatório")
  UUID patientId,
  
  @NotNull(message = "Data e hora são obrigatórias")
  @Future(message = "A data do agendamento deve ser no futuro")
  LocalDateTime dateTime,
  
  @NotNull(message = "Duração é obrigatória")
  @Positive(message = "Duração deve ser um valor positivo")
  Integer durationMinutes,
  
  @Size(max = 1000, message = "Notas não podem exceder 1000 caracteres")
  String notes) {
}
