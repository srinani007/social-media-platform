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
            System.out.println("Checking for admin user...");
            if (userRepository.findByUsername("admin").isPresent()) {
                User admin = userRepository.findByUsername("admin").get();
                admin.setPassword(encoder.encode("newadminpassword"));
                admin.setRole("ROLE_ADMIN");
                admin.setPro(true);
                userRepository.save(admin);
                System.out.println("✅ Updated existing admin user.");
            } else {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("newadminpassword"));
                admin.setRole("ROLE_ADMIN");
                admin.setPro(true);
                userRepository.save(admin);
                System.out.println("✅ Created new admin user.");
            }
        };
    }
}
