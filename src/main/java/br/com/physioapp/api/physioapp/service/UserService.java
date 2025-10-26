package br.com.physioapp.api.physioapp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class UserService {

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
  
  @Transactional
  public User createUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Transactional
  public User updateUser(UUID id, User userDetails) {
    User existingUser = getUserById(id);

    existingUser.setFullname(userDetails.getFullname());
    existingUser.setEmail(userDetails.getEmail());

    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
      existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
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

}
