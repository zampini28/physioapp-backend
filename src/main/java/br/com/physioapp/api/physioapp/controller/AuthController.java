package br.com.physioapp.api.physioapp.controller;

import br.com.physioapp.api.physioapp.dto.*;
import br.com.physioapp.api.physioapp.events.UserCreatedEvent;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.service.JwtService;
import br.com.physioapp.api.physioapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final JwtService jwtService;
  private final ApplicationEventPublisher eventPublisher;

  public AuthController(UserService userService,
      JwtService jwtService,
      ApplicationEventPublisher eventPublisher) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.eventPublisher = eventPublisher;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    UserResponse created;
    if (request.userType() == UserType.PATIENT) {
      var patientReq = new CreatePatientRequest(request.fullname(), request.email(), request.password());
      created = userService.createPatient(patientReq);
    } else if (request.userType() == UserType.PHYSIO) {
      var physioReq = new CreatePhysiotherapistRequest(
          request.fullname(), request.email(), request.password(), request.crefito());
      created = userService.createPhysiotherapist(physioReq);
    } else {
      return ResponseEntity.badRequest().build();
    }

    eventPublisher.publishEvent(new UserCreatedEvent(created));

    URI location = URI.create("/users/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    var user = userService.validateUserForAuth(request.email(), request.password());
    String token = jwtService.generateToken(user);

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

    return ResponseEntity.ok().headers(headers).body(new AuthResponse(token));
  }
}
