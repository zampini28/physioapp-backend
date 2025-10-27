package br.com.physioapp.api.physioapp.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.physioapp.api.physioapp.model.NotificationType;

public record NotificationResponseDTO(
    UUID id,
    UUID recipientId,
    String title,
    String message,
    NotificationType type,
    Boolean read,
    LocalDateTime createdAt) {}