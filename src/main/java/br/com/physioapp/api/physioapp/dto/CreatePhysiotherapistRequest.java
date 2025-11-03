package br.com.physioapp.api.physioapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePhysiotherapistRequest(
  @NotBlank(message = "Nome completo é obrigatório")
  String fullname,

  @NotBlank(message = "E-mail é obrigatório")
  @Email(message = "E-mail inválido")
  String email,

  @NotBlank(message = "Senha é obrigatória")
  @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
  String password,

  @NotBlank(message = "Número do CREFITO é obrigatório")
  String crefito
) {}
