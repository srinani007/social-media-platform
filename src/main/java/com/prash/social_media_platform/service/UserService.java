package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public User getByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmailIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // Spring Security needs "ROLE_" prefix
        userRepository.save(user);
    }
    public void updateUser(User updated) {
        userRepository.save(updated); // no password encoding or role override
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public User getByUsername(String username) {
        System.out.println("Fetching user by username: " + username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void setProStatus(Long id, boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPro(status);
        userRepository.save(user);
    }

    public void upgradeToPro(String username) {
        User user = getByUsername(username);
        user.setPro(true);
        userRepository.save(user);
    }
    public void setRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}