package br.com.physioapp.api.physioapp.dto;

import java.util.UUID;

import br.com.physioapp.api.physioapp.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificationRequestDTO(
  @NotNull(message = "ID do destinatário é obrigatório")
  UUID recipientId,
  
  @NotBlank(message = "Título é obrigatório")
  @Size(max = 100, message = "Título não pode exceder 100 caracteres")
  String title,
  
  @NotBlank(message = "Mensagem é obrigatória")
  @Size(max = 500, message = "Mensagem não pode exceder 500 caracteres")
  String message,
  
  @NotNull(message = "Tipo da notificação é obrigatório")
  NotificationType type) {
}
