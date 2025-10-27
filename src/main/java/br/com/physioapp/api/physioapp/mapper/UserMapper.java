package br.com.physioapp.api.physioapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import br.com.physioapp.api.physioapp.dto.UserResponse;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "crefito", source = ".", qualifiedByName = "extractCrefito")
  @Mapping(target = "id", source = "id")
  @Mapping(target = "fullname", source = "fullname")
  @Mapping(target = "email", source = "email")
  @Mapping(target = "type", source = "type")
  UserResponse toResponse(User user);

  UserResponse toResponse(UserRepository.Summary summary);

  @Named("extractCrefito")
  default String extractCrefito(User user) {
    if (user instanceof Physiotherapist) {
      return ((Physiotherapist) user).getCrefito();
    }
    return null;
  }
}
