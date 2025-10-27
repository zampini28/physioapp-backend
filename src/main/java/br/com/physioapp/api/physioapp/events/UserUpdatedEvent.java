package br.com.physioapp.api.physioapp.events;

import java.util.Objects;

import br.com.physioapp.api.physioapp.dto.UserResponse;

public final class UserUpdatedEvent {

  private final UserResponse user;

  public UserUpdatedEvent(UserResponse user) {
    this.user = Objects.requireNonNull(user, "user");
  }

  public UserResponse user() {
    return user;
  }
}
