package com.prash.social_media_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String home(Principal principal) {
        if (principal != null) {
            // user is authenticated (or remembered) → go to dashboard
            return "redirect:/dashboard";
        }
        // not logged in → show login page
        return "login";
        // (or "index" if your login template is named index.html)
    }
}
