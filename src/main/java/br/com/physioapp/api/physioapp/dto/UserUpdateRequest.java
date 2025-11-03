package br.com.physioapp.api.physioapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
  @NotBlank(message = "Nome completo é obrigatório")
  String fullname,
  
  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "E-mail inválido")
  String email,
  
  @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
  String password
) {}