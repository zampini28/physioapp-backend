package br.com.physioapp.api.physioapp.dto;

public record AuthRequest(
    String email,
    String password) {
}
