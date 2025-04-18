package com.prash.social_media_platform.security;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            // Debug logging - add this to see all attributes
            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.debug("OAuth2 User Attributes: {}", attributes);

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");

            if (email == null) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("missing_email", "Email not found in OAuth2 user attributes", null)
                );
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUsername(name != null ? name : email);
                        newUser.setEmail(email);
                        newUser.setPassword("{noop}OAUTH2_USER");
                        newUser.setRole("ROLE_USER");
                        newUser.setPro(false);
                        return userRepository.save(newUser);
                    });

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                    attributes,  // Using the full attributes map
                    "email"     // Name attribute key
            );
        } catch (Exception ex) {
            log.error("OAuth2 authentication failed", ex);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("authentication_error", "OAuth2 authentication failed", null),
                    ex
            );
        }
    }
}