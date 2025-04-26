package com.prash.social_media_platform.security;

import com.prash.social_media_platform.model.AuthProvider;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to default implementation for loading user attributes
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_email"),
                    "Email not provided by OAuth2 provider");
        }

        // Try to fetch existing user
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    log.info("Creating new user for OAuth2 login: {}", email);
                    User newUser = new User();
                    newUser.setUsername(email.substring(0, email.indexOf("@")));
                    newUser.setEmail(email);
                    newUser.setFullName(oauth2User.getAttribute("name"));
                    newUser.setProfilePictureUrl(oauth2User.getAttribute("picture"));
                    newUser.setAuthProvider(AuthProvider.GOOGLE);
                    // Create a random secure password, not used for login
                    newUser.setPassword(encoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(newUser);
                });



        // Add custom attributes for session
        Map<String, Object> mappedAttrs = new HashMap<>(oauth2User.getAttributes());
        mappedAttrs.put("db_user_id", user.getId());
        mappedAttrs.put("db_username", user.getUsername());

        // Return Spring Security user
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                mappedAttrs,
                "email"
        );
    }
}
