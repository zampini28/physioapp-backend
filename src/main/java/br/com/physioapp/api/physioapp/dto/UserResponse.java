package br.com.physioapp.api.physioapp.dto;

import java.util.UUID;

import br.com.physioapp.api.physioapp.model.UserType;

public record UserResponse(
  UUID id,
  String fullname,
  String email,
  UserType userType,
  String crefito
) {
}
