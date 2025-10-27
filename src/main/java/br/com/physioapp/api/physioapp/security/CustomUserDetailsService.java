package br.com.physioapp.api.physioapp.security;

import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.management.relation.Role;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userService) {
        this.userRepository = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> maybeUser = userRepository.findByEmail(username);
        User user = maybeUser.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return toUserDetails(user);
    }

    public UserDetails loadUserByIdString(String idString) {
        UUID id;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException ex) {
            throw new UsernameNotFoundException("Invalid user id: " + idString);
        }
        Optional<User> maybeUser = userRepository.findById(id);
        User user = maybeUser.orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        return toUserDetails(user);
    }

    private UserDetails toUserDetails(User user) {
        Collection<GrantedAuthority> authorities = mapRolesToAuthorities(user);
        String principalName = user.getId() != null ? user.getId().toString() : user.getEmail();

        return org.springframework.security.core.userdetails.User
                .withUsername(principalName)
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!isEnabled(user))
                .build();
    }

    private boolean isEnabled(User user) {
        try {
            return user.isActive();
        } catch (NoSuchMethodError | NullPointerException ex) {
            log.debug("User.isActive() not present or null, defaulting to enabled for user {}", user.getEmail());
            return true;
        }
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(User user) {
        try {
            Set<Role> roles = user.getRoles();
            if (roles == null)
                return List.of();
            return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList());
        } catch (NoSuchMethodError ex) {
            try {
                String type = (String) user.getClass().getMethod("getType").invoke(user);
                if (type == null)
                    return List.of();
                return List.of(new SimpleGrantedAuthority("ROLE_" + type));
            } catch (Exception e) {
                log.debug("No roles/type found on User, returning empty authorities");
                return List.of();
            }
        } catch (Exception ex) {
            log.error("Error mapping roles for user {}", user.getEmail(), ex);
            return List.of();
        }
    }
}
