package br.com.physioapp.api.physioapp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.CreatePatientRequest;
import br.com.physioapp.api.physioapp.dto.CreatePhysiotherapistRequest;
import br.com.physioapp.api.physioapp.dto.UserResponse;
import br.com.physioapp.api.physioapp.dto.UserUpdateRequest;
import br.com.physioapp.api.physioapp.exception.AuthenticationException;
import br.com.physioapp.api.physioapp.exception.CrefitoAlreadyExistsException;
import br.com.physioapp.api.physioapp.exception.EmailAlreadyExistsException;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.repository.PhysiotherapistRepository;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private PhysiotherapistRepository physiotherapistRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public User getUserById(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID usuário não encontrado: " + id));
  }

  public User validateUser(String email, String rawPassword) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("Credenciais inválidas."));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new AuthenticationException("Credenciais inválidas.");
    }

    return user;
  }

  @Transactional
  public Patient createPatient(CreatePatientRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + request.email());
    }

    Patient patient = new Patient();
    patient.setFullname(request.fullName());
    patient.setEmail(request.email());
    patient.setPassword(passwordEncoder.encode(request.password()));
    patient.setType(UserType.PATIENT);

    return userRepository.save(patient);
  }

  @Transactional
  public Physiotherapist createPhysiotherapist(CreatePhysiotherapistRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + request.email());
    }

    if (request.crefito() == null || request.crefito().isBlank()) {
      throw new IllegalArgumentException("CREFITO é obrigatório para fisioterapeutas.");
    }

    if (physiotherapistRepository.findByCrefito(request.crefito()).isPresent()) {
      throw new CrefitoAlreadyExistsException("CREFITO já cadastrado: " + request.crefito());
    }

    Physiotherapist physio = new Physiotherapist();
    physio.setFullname(request.fullName());
    physio.setEmail(request.email());
    physio.setPassword(passwordEncoder.encode(request.password()));
    physio.setCrefito(request.crefito());
    physio.setType(UserType.PHYSIO);

    return userRepository.save(physio);
  }

  @Transactional
  public User updateUser(UUID id, UserUpdateRequest userDetails) {
    User existingUser = getUserById(id);

    existingUser.setFullname(userDetails.fullName());
    existingUser.setEmail(userDetails.email());

    if (userDetails.password() != null && !userDetails.password().isBlank()) {
      existingUser.setPassword(passwordEncoder.encode(userDetails.password()));
    }

    return userRepository.save(existingUser);
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + id);
    }
    userRepository.deleteById(id);
  }
  
  @Transactional(readOnly = true)
  public UserResponse getUserProfile(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + userId));

    String crefito = null;

    if (user instanceof Physiotherapist) {
      crefito = ((Physiotherapist) user).getCrefito();
    }

    return new UserResponse(
        user.getId(),
        user.getFullname(),
        user.getEmail(),
        user.getType(),
        crefito);
  }

}
