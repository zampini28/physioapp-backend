package br.com.physioapp.api.physioapp.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID Usuário não encontrado: " + id));
    }

    @Transactional
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setFullname(userDetails.getFullname());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhone(userDetails.getPhone());
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("ID Usuário não encontrado: " + id);
        }
        userRepository.deleteById(id);
    }
}