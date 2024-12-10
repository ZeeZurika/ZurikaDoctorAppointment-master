package org.zurika.zeehealth.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Displaying the login page
    @GetMapping("/")
    public String showLoginPage(Model model) {
        model.addAttribute("title", "Login Page");
        return "login"; // HTML page for login
    }

    // Redirect to the appropriate dashboard after login
    @GetMapping("/redirect-dashboard")
    public String redirectDashboard() {
        // Get the currently logged-in user's role
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();

        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_DOCTOR" -> "redirect:/doctor/dashboard";
            case "ROLE_PATIENT" -> "redirect:/patient/dashboard";
            default -> "redirect:/?error=true";
        };
    }
}

