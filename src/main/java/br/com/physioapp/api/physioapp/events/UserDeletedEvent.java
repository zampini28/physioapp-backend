package br.com.physioapp.api.physioapp.events;

import java.util.Objects;
import java.util.UUID;

public final class UserDeletedEvent {

  private final UUID userId;

  public UserDeletedEvent(UUID userId) {
    this.userId = Objects.requireNonNull(userId, "userId");
  }

  public UUID userId() {
    return userId;
  }
}
