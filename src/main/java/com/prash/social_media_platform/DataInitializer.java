package com.prash.social_media_platform;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("adminpassword"));
                admin.setRole("ROLE_ADMIN");
                admin.setPro(true);
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPassword(encoder.encode("userpassword"));
                user.setRole("ROLE_USER");
                user.setPro(false);
                userRepository.save(user);
            }
        };
    }
}
