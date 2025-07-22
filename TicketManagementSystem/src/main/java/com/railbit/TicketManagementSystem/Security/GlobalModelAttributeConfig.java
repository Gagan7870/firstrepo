package com.railbit.TicketManagementSystem.Security;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeConfig {

    @ModelAttribute
    public void addUserDetails(Model model, Principal principal) {
        if (principal != null) {
            System.out.println("Logged-in user: " + principal.getName());
            model.addAttribute("username", principal.getName());
        } else {
            System.out.println("No authenticated user (anonymous)");
            model.addAttribute("username", "anonymous");
        }
    }
}


