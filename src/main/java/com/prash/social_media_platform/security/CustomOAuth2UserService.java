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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // delegate to Spring’s default loader
        OAuth2User superUser = super.loadUser(userRequest);
        String email = superUser.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_email"),
                    "Email missing from OAuth2 provider");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    log.info("→ saving new OAuth2 user for {}", email);
                    User u = new User();
                    u.setUsername(email.substring(0, email.indexOf("@")));
                    u.setEmail(email);
                    // must satisfy @NotBlank & @Size(min=8)
                    u.setPassword(UUID.randomUUID().toString());
                    u.setRole("ROLE_USER");
                    u.setPro(false);
                    return userRepository.save(u);
                });

        // merge in all the attributes + our DB fields
        Map<String, Object> attrs = new HashMap<>(superUser.getAttributes());
        attrs.put("db_user_id", user.getId());
        attrs.put("db_username", user.getUsername());

        // **use** "db_username" as the name key so auth.getName()==username
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attrs,
                "email"
        );
    }
}
