package br.com.physioapp.api.physioapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.physioapp.api.physioapp.dto.AuthRequest;
import br.com.physioapp.api.physioapp.dto.AuthResponse;
import br.com.physioapp.api.physioapp.dto.CreatePatientRequest;
import br.com.physioapp.api.physioapp.dto.CreatePhysiotherapistRequest;
import br.com.physioapp.api.physioapp.dto.RegisterRequest;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.service.JwtService;
import br.com.physioapp.api.physioapp.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody RegisterRequest user) {
    try {
      if (user.userType() == UserType.PATIENT) {
        CreatePatientRequest patientRequest = new CreatePatientRequest(
            user.fullname(),
            user.email(),
            user.password());

        userService.createPatient(patientRequest);
      } else if (user.userType() == UserType.PHYSIO) {
        CreatePhysiotherapistRequest physiotherapistRequest = new CreatePhysiotherapistRequest(
            user.fullname(),
            user.email(),
            user.password(),
            user.crefito());

        userService.createPhysiotherapist(physiotherapistRequest);
      } else {
        System.out.println("Invalid user type: " + user.userType());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }

      return ResponseEntity.status(HttpStatus.CREATED).build(); 
    } catch (IllegalArgumentException e) {
      System.out.println("Error during user registration: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    User user = userService.validateUser(request.email(), request.password());

    String token = jwtService.generateToken(user);

    return ResponseEntity.ok(new AuthResponse(token));
  }

}
