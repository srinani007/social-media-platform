package com.prash.social_media_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        // This returns the template "index.html" from src/main/resources/templates
        return "index";
    }
}
