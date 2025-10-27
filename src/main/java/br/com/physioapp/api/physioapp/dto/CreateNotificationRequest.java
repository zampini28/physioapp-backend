package br.com.physioapp.api.physioapp.dto;

import java.util.UUID;

import br.com.physioapp.api.physioapp.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNotificationRequest(
    @NotNull
    UUID recipientId,
    
    @NotBlank
    String title,

    @NotBlank
    String message,

    @NotNull
    NotificationType type) {}
