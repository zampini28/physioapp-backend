package br.com.physioapp.api.physioapp.mapper;

import org.springframework.stereotype.Component;

import br.com.physioapp.api.physioapp.dto.UserResponse;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;

@Component
public class UserManualMapper {

  public UserResponse toResponse(User user) {
    if (user == null) return null;
    String crefito = null;
    if (user instanceof Physiotherapist) {
      crefito = ((Physiotherapist) user).getCrefito();
    }
    return new UserResponse(
      user.getId(),
      user.getFullname(),
      user.getEmail(),
      user.getType(),
      crefito
    );
  }
}
