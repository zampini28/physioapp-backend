package br.com.physioapp.api.physioapp.controller;

import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.repository.UserRepository;
import br.com.physioapp.api.physioapp.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  private Patient testPatient;
  private Physiotherapist testPhysio;
  private String patientToken;
  private String physioToken;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();

    testPatient = new Patient();
    testPatient.setFullname("Test Patient");
    testPatient.setEmail("patient@example.com");
    testPatient.setPassword(passwordEncoder.encode("password123"));
    testPatient.setType(UserType.PATIENT);
    userRepository.save(testPatient);

    testPhysio = new Physiotherapist();
    testPhysio.setFullname("Test Physio");
    testPhysio.setEmail("physio@example.com");
    testPhysio.setPassword(passwordEncoder.encode("password456"));
    testPhysio.setType(UserType.PHYSIO);
    testPhysio.setCrefito("12345-BR");
    userRepository.save(testPhysio);

    patientToken = jwtService.generateToken(testPatient);
    physioToken = jwtService.generateToken(testPhysio);
  }

  @Test
  void testGetMyProfile_AsPatient_Success() throws Exception {
    mockMvc.perform(get("/users/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testPatient.getId().toString()))
        .andExpect(jsonPath("$.fullname").value("Test Patient"))
        .andExpect(jsonPath("$.email").value("patient@example.com"))
        .andExpect(jsonPath("$.userType").value("PATIENT"))
        .andExpect(jsonPath("$.crefito").value(nullValue()));
  }

  @Test
  void testGetMyProfile_AsPhysio_Success() throws Exception {
    mockMvc.perform(get("/users/me")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + physioToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testPhysio.getId().toString()))
        .andExpect(jsonPath("$.fullname").value("Test Physio"))
        .andExpect(jsonPath("$.email").value("physio@example.com"))
        .andExpect(jsonPath("$.userType").value("PHYSIO"))
        .andExpect(jsonPath("$.crefito").value("12345-BR"));
  }

  @Test
  void testGetMyProfile_Fail_Unauthenticated() throws Exception {
    mockMvc.perform(get("/users/me"))
        .andExpect(status().isForbidden());
  }
}
