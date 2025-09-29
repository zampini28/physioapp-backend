package br.com.physioapp.api.physioapp.dto;

import br.com.physioapp.api.physioapp.model.NotificationType;

public record NotificationRequestDTO(
    Long recipientId,
    String title,
    String message,
    NotificationType type
) {}
