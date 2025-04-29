package com.prash.social_media_platform.config;

import com.prash.social_media_platform.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserDetailsService    userDetailsService;
    private final Environment           env;

    public SecurityConfig(@Lazy CustomOAuth2UserService customOAuth2UserService,
                          UserDetailsService userDetailsService,
                          Environment env) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userDetailsService      = userDetailsService;
        this.env                     = env;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // on prod, force HTTPS
        if (env.acceptsProfiles(Profiles.of("prod"))) {
            http.requiresChannel(c -> c.anyRequest().requiresSecure());
        }

        http
                .authenticationProvider(daoAuthProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/search", "/posts/**", "/login", "/register", "/oauth2/**",
                                "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                "/forgot-password", "/reset-password/**", "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/messages/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(f -> f
                        .loginPage("/login")
                        .defaultSuccessUrl("/user/dashboard", true)
                        .permitAll()
                )

                .oauth2Login(o -> o
                        .loginPage("/login")
                        .defaultSuccessUrl("/user/dashboard", true)
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                )

                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID","remember-me")
                        .permitAll()
                )

                .rememberMe(r -> r
                        .key("aVerySecretKey")
                        .tokenValiditySeconds(7*24*60*60)
                )

                // **Re-enable** CSRF via a cookie-based repo so your <meta> tags and JS work:
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }
}
