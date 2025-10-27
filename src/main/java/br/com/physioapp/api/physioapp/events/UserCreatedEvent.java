package br.com.physioapp.api.physioapp.events;

import java.util.Objects;

import br.com.physioapp.api.physioapp.dto.UserResponse;

public final class UserCreatedEvent {

  private final UserResponse user;

  public UserCreatedEvent(UserResponse user) {
    this.user = Objects.requireNonNull(user, "user");
  }

  public UserResponse user() {
    return user;
  }
}
