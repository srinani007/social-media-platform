package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameOrEmailIgnoreCase(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmailIgnoreCase(String email);
}