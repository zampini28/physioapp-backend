package br.com.physioapp.api.physioapp.dto;

import java.util.UUID;

import br.com.physioapp.api.physioapp.model.NotificationType;

public record NotificationRequestDTO(
    UUID recipientId,
    String title,
    String message,
    NotificationType type) {
}
