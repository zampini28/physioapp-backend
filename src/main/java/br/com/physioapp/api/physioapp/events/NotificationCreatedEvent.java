package br.com.physioapp.api.physioapp.events;

import br.com.physioapp.api.physioapp.dto.NotificationResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class NotificationCreatedEvent {
    private final NotificationResponseDTO notification;
}