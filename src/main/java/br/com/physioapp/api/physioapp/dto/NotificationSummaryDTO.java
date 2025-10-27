package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.physioapp.api.physioapp.model.NotificationType;

public record NotificationSummaryDTO(
    UUID id,
    String title,
    NotificationType type,
    Boolean isRead,
    LocalDateTime createdAt,
    UUID recipientId) {
}
