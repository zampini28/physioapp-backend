package br.com.physioapp.api.physioapp.dto;

import br.com.physioapp.api.physioapp.model.UserType;

public record RegisterRequest(
    String fullname,
    String email,
    String password,
    UserType userType,
    String crefito) {
}
