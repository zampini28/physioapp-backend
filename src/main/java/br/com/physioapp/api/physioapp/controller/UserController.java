package br.com.physioapp.api.physioapp.controller;

import br.com.physioapp.api.physioapp.dto.UserResponse;
import br.com.physioapp.api.physioapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/me")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      return ResponseEntity.status(401).build();
    }

    UUID userId;
    try {
      userId = UUID.fromString(authentication.getName());
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.status(401).build();
    }

    UserResponse userProfile = userService.getUserProfile(userId);
    return ResponseEntity.ok(userProfile);
  }
}
