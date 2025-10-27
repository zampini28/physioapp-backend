package br.com.physioapp.api.physioapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.physioapp.api.physioapp.dto.AuthRequest;
import br.com.physioapp.api.physioapp.dto.RegisterRequest;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @SuppressWarnings("unused")
  private User testUser;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    Patient testPatient = new Patient();

    testPatient.setFullname("Test User");
    testPatient.setEmail("test@example.com");
    testPatient.setPassword(passwordEncoder.encode("password123"));
    testPatient.setType(UserType.PATIENT);

    this.testUser = userRepository.save(testPatient);
  }

  @Test
  void testRegister_Patient_Success() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "New Patient",
        "patient@example.com",
        "newpassword123",
        UserType.PATIENT,
        null);

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    Optional<User> newUser = userRepository.findByEmail("patient@example.com");
    assertTrue(newUser.isPresent(), "User should be created in the database");
    assertEquals("New Patient", newUser.get().getFullname());
    assertEquals(UserType.PATIENT, newUser.get().getType());
  }

  @Test
  void testRegister_Physio_Success() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "New Physio",
        "physio@example.com",
        "physiopassword",
        UserType.PHYSIO,
        "12345-BR");

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    Optional<User> newUser = userRepository.findByEmail("physio@example.com");
    assertTrue(newUser.isPresent(), "Physio user should be created");
    assertEquals(UserType.PHYSIO, newUser.get().getType());
  }

  @Test
  void testRegister_Physio_Fail_NoCrefito() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "New Physio NoCrefito",
        "physio-fail@example.com",
        "physiopassword",
        UserType.PHYSIO,
        "");

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegister_Fail_EmailAlreadyExists() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "Another User",
        "test@example.com",
        "password123",
        UserType.PATIENT,
        null);

    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  @Test
  void testLogin_Success() throws Exception {
    AuthRequest request = new AuthRequest(
        "test@example.com",
        "password123");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  void testLogin_Fail_WrongPassword() throws Exception {
    AuthRequest request = new AuthRequest(
        "test@example.com",
        "wrongpassword");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testLogin_Fail_UserNotFound() throws Exception {
    AuthRequest request = new AuthRequest(
        "nouser@example.com",
        "password123");

    mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }
}
