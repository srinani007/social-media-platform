package com.prash.social_media_platform.config;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CsrfControllerAdvice {

    @ModelAttribute("_csrf")
    public CsrfToken csrf(CsrfToken token) {
        // Spring will auto-resolve CsrfToken if CSRF is enabled
        return token;
    }
}
