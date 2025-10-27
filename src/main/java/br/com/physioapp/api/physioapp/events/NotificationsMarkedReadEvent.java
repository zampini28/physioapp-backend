package br.com.physioapp.api.physioapp.events;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class NotificationsMarkedReadEvent {
    private final UUID userId;
    private final int updatedCount;
}
