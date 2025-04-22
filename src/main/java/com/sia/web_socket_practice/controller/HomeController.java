package com.sia.web_socket_practice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homePage(Model model, Principal principal){
        String username = (principal == null) ? "anonymous" : principal.getName();
        model.addAttribute("username", username);
        return "home";
    }
}
