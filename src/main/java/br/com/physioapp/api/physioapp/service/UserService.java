package br.com.physioapp.api.physioapp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.CreatePatientRequest;
import br.com.physioapp.api.physioapp.dto.CreatePhysiotherapistRequest;
import br.com.physioapp.api.physioapp.dto.UserResponse;
import br.com.physioapp.api.physioapp.dto.UserUpdateRequest;
import br.com.physioapp.api.physioapp.events.UserCreatedEvent;
import br.com.physioapp.api.physioapp.events.UserDeletedEvent;
import br.com.physioapp.api.physioapp.events.UserUpdatedEvent;
import br.com.physioapp.api.physioapp.exception.AuthenticationException;
import br.com.physioapp.api.physioapp.exception.CrefitoAlreadyExistsException;
import br.com.physioapp.api.physioapp.exception.EmailAlreadyExistsException;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.mapper.UserMapper;
import br.com.physioapp.api.physioapp.model.Patient;
import br.com.physioapp.api.physioapp.model.Physiotherapist;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.model.UserType;
import br.com.physioapp.api.physioapp.repository.PhysiotherapistRepository;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class UserService {

  private final PhysiotherapistRepository physiotherapistRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final UserMapper mapper;
  private final int defaultPageSize = 50;

  public UserService(PhysiotherapistRepository physiotherapistRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      ApplicationEventPublisher eventPublisher,
      UserMapper mapper) {
    this.physiotherapistRepository = physiotherapistRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  public Page<UserResponse> getAllUsers(int page, int size) {
    Pageable p = PageRequest.of(page, Math.min(size, defaultPageSize));
    return userRepository.findAllProjectedBy(p).map(mapper::toResponse);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID id) {
    User user = userRepository.findByIdWithType(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID usuário não encontrado: " + id));
    return mapper.toResponse(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getUserProfile(UUID userId) {
    return getUserById(userId);
  }

  @Transactional
  public UserResponse createPatient(CreatePatientRequest req) {
    validateEmailAndPassword(req.email(), req.password());
    if (userRepository.existsByEmail(req.email())) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + req.email());
    }

    Patient p = new Patient();
    p.setFullname(req.fullName());
    p.setEmail(req.email());
    p.setPassword(passwordEncoder.encode(req.password()));
    p.setType(UserType.PATIENT);

    try {
      User saved = userRepository.save(p);
      UserResponse resp = mapper.toResponse(saved);
      eventPublisher.publishEvent(new UserCreatedEvent(resp));
      return resp;
    } catch (DataIntegrityViolationException ex) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + req.email());
    }
  }

  @Transactional
  public UserResponse createPhysiotherapist(CreatePhysiotherapistRequest req) {
    validateEmailAndPassword(req.email(), req.password());
    if (req.crefito() == null || req.crefito().isBlank()) {
      throw new IllegalArgumentException("CREFITO é obrigatório para fisioterapeutas.");
    }

    if (userRepository.existsByEmail(req.email())) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + req.email());
    }

    if (physiotherapistRepository.existsByCrefito(req.crefito())) {
      throw new CrefitoAlreadyExistsException("CREFITO já cadastrado: " + req.crefito());
    }

    Physiotherapist ph = new Physiotherapist();
    ph.setFullname(req.fullName());
    ph.setEmail(req.email());
    ph.setPassword(passwordEncoder.encode(req.password()));
    ph.setCrefito(req.crefito());
    ph.setType(UserType.PHYSIO);

    try {
      User saved = userRepository.save(ph);
      UserResponse resp = mapper.toResponse(saved);
      eventPublisher.publishEvent(new UserCreatedEvent(resp));
      return resp;
    } catch (DataIntegrityViolationException ex) {
      if (physiotherapistRepository.existsByCrefito(req.crefito())) {
        throw new CrefitoAlreadyExistsException("CREFITO já cadastrado: " + req.crefito());
      }
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + req.email());
    }
  }

  @Transactional
  public UserResponse updateUser(UUID id, UserUpdateRequest dto) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ID usuário não encontrado: " + id));

    if (dto.email() != null && !dto.email().equalsIgnoreCase(existing.getEmail())
        && userRepository.existsByEmail(dto.email())) {
      throw new EmailAlreadyExistsException("E-mail já cadastrado: " + dto.email());
    }

    existing.setFullname(dto.fullName());
    if (dto.email() != null && !dto.email().isBlank())
      existing.setEmail(dto.email());
    if (dto.password() != null && !dto.password().isBlank()) {
      validatePasswordPolicy(dto.password());
      existing.setPassword(passwordEncoder.encode(dto.password()));
    }

    User saved = userRepository.save(existing);
    UserResponse resp = mapper.toResponse(saved);
    eventPublisher.publishEvent(new UserUpdatedEvent(resp));
    return resp;
  }

  @Transactional
  public void deleteUser(UUID id) {
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + id);
    }
    userRepository.deleteById(id);
    eventPublisher.publishEvent(new UserDeletedEvent(id));
  }

  @Transactional(readOnly = true)
  public User validateUserForAuth(String email, String rawPassword) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new AuthenticationException("Credenciais inválidas."));

    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
      throw new AuthenticationException("Credenciais inválidas.");
    }
    return user;
  }

  private void validateEmailAndPassword(String email, String password) {
    if (email == null || email.isBlank())
      throw new IllegalArgumentException("Email obrigatório");
    validatePasswordPolicy(password);
  }

  private void validatePasswordPolicy(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Senha deve ter ao menos 8 caracteres");
    }
    boolean hasDigit = password.chars().anyMatch(Character::isDigit);
    boolean hasLetter = password.chars().anyMatch(Character::isLetter);
    if (!hasDigit || !hasLetter) {
      throw new IllegalArgumentException("Senha deve conter letras e números");
    }
  }
}
