package br.com.physioapp.api.physioapp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.physioapp.api.physioapp.dto.CreatePatientRequest;
import br.com.physioapp.api.physioapp.dto.CreatePhysiotherapistRequest;
import br.com.physioapp.api.physioapp.dto.RegisterRequest;
import br.com.physioapp.api.physioapp.dto.UserUpdateRequest;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<Void> createUser(@RequestBody RegisterRequest user) {
    if (user.userType() == UserType.PHYSIO && (user.crefito() == null || user.crefito().isBlank())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (user.userType() == UserType.PATIENT) {
      CreatePatientRequest patientRequest = new CreatePatientRequest(
          user.fullname(),
          user.email(),
          user.password());
      userService.createPatient(patientRequest);
      return ResponseEntity.status(HttpStatus.CREATED).build();

    } else if (user.userType() == UserType.PHYSIO) {
      CreatePhysiotherapistRequest physiotherapistRequest = new CreatePhysiotherapistRequest(
          user.fullname(),
          user.email(),
          user.password(),
          user.crefito());
      userService.createPhysiotherapist(physiotherapistRequest);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable UUID id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest userDetails) {
    User updatedUser = userService.updateUser(id, userDetails);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}