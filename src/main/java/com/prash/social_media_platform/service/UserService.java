package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Find by email (case‑insensitive).
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Find by username (case‑sensitive).
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Lookup by username OR email (case‑insensitive).
     * Throws if not found.
     */
    @Cacheable("users")
    public User getByUsernameOrEmail(String identifier) {
        System.out.println("Fetching user from DB: " + identifier);
        return userRepository
                .findByUsernameOrEmailIgnoreCase(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
    }


    /**
     * Unified lookup for controllers:
     * delegates to getByUsernameOrEmail(...)
     */
    public User getByUsername(String identifier) {
        return getByUsernameOrEmail(identifier);
    }

    /**
     * Register a new user (form‑signup).
     * Encodes password and sets ROLE_USER.
     */
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    /**
     * Update profile fields (username, email, etc.).
     */
    public void updateUser(User updated) {
        userRepository.save(updated);
    }

    /**
     * Return all users (for admin dashboard).
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Delete a user by ID.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Grant or revoke Pro status.
     */
    public void setProStatus(Long id, boolean status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setPro(status);
        userRepository.save(user);
    }

    /**
     * Mark a user Pro by their username/email identifier.
     */
    public void upgradeToPro(String identifier) {
        User user = getByUsernameOrEmail(identifier);
        user.setPro(true);
        userRepository.save(user);
    }

    /**
     * Change a user’s role by their database ID.
     */
    public void setRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setRole(role);
        userRepository.save(user);
    }

    /**
     * Get by email, throwing Spring Security’s exception if not found.
     */
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public List<User> findAllExcept(String me) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getUsername().equalsIgnoreCase(me))
                .collect(Collectors.toList());
    }
}
