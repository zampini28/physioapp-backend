package br.com.physioapp.api.physioapp.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.physioapp.api.physioapp.dto.AuthRequest;
import br.com.physioapp.api.physioapp.dto.AuthResponse;
import br.com.physioapp.api.physioapp.dto.RegisterRequest;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.repository.UserRepository;
import br.com.physioapp.api.physioapp.service.JwtService;
import br.com.physioapp.api.physioapp.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtService jwtService) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
    User toCreate;
    if (request.userType() == UserType.PHYSIO) {
      Physiotherapist physio = new Physiotherapist();
      physio.setFullname(request.fullname());
      physio.setEmail(request.email());
      physio.setPassword(request.password());
      physio.setType(UserType.PHYSIO);
      physio.setCrefito(request.crefito());
      toCreate = physio;
    } else {
      Patient patient = new Patient();
      patient.setFullname(request.fullname());
      patient.setEmail(request.email());
      patient.setPassword(request.password());
      patient.setType(UserType.PATIENT);
      toCreate = patient;
    }

    User created = userService.createUser(toCreate);

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    Optional<User> userOpt = userRepository.findByEmail(request.email());
    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user = userOpt.get();
    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = jwtService.generateToken(user);
    return ResponseEntity.ok(new AuthResponse(token));
  }

}
