package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
  select u
    from User u
   where lower(u.username) = lower(:username)
      or lower(u.email)    = lower(:username)
""")
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("username") String username);
    Optional<User> findByResetToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmailIgnoreCase(String email);
}