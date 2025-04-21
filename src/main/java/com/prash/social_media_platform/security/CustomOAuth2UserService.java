package com.prash.social_media_platform.security;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();

            String email = oAuth2User.getAttribute("email");
            if (email == null) throw new OAuth2AuthenticationException(new OAuth2Error("invalid_email"), "Email missing");

            log.info("Processing OAuth2 login for email: {}", email);

            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseGet(() -> {
                        try {
                            User newUser = new User();

                            // Generate username from email, ensuring it meets length requirements
                            String username = email.split("@")[0];
                            if (username.length() < 3) {
                                username = username + "_user";  // Ensure minimum length
                            }
                            if (username.length() > 50) {
                                username = username.substring(0, 50);  // Ensure maximum length
                            }

                            // Check if username exists
                            if (userRepository.findByUsername(username).isPresent()) {
                                // Username exists, make it unique by adding a timestamp
                                username = username + System.currentTimeMillis() % 10000;
                            }

                            newUser.setUsername(username);
                            newUser.setEmail(email);

                            // Generate secure random password
                            String securePassword = passwordEncoder.encode("OAuthUser" + System.currentTimeMillis());
                            newUser.setPassword(securePassword);

                            newUser.setRole("ROLE_USER");

                            log.info("Creating new OAuth user with username: {}", username);
                            return userRepository.save(newUser);
                        } catch (Exception e) {
                            log.error("Error creating OAuth user", e);
                            throw new OAuth2AuthenticationException(
                                    new OAuth2Error("user_creation_error"),
                                    "Failed to create user: " + e.getMessage(),
                                    e);
                        }
                    });

            // Add user info to attributes
            attributes.put("db_user_id", user.getId());
            attributes.put("db_username", user.getUsername());

            String nameKey = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                    attributes,
                    nameKey
            );
        } catch (Exception e) {
            log.error("OAuth2 authentication error", e);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("authentication_error"),
                    "Authentication failed: " + e.getMessage(),
                    e);
        }
    }
}