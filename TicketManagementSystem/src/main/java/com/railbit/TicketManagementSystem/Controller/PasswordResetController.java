package com.railbit.TicketManagementSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.railbit.TicketManagementSystem.Service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordResetController {
    

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forget-password")
    public String showForgotPasswordForm() {
        return "forget-password";
    }

    @PostMapping("/send-reset-link")
    public String processForgotPassword(HttpServletRequest request,
                                        @RequestParam("email") String email,
                                        RedirectAttributes attrs) {
        // ✅ Fix: Construct appUrl properly
        String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        try {
            passwordResetService.processForgotPassword(email, appUrl);
            attrs.addFlashAttribute("message", "Password reset link has been sent to your email.");
        } catch (Exception e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/forget-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                        @RequestParam("userType") String userType,
                                        Model model) {
        try {
            passwordResetService.findByTokenAndUserType(token, userType);
            model.addAttribute("token", token);
            model.addAttribute("userType", userType);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reset-password-error";
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("userType") String userType,
                                       @RequestParam("password") String password,
                                       Model model) {
        try {
            Object entity = passwordResetService.findByTokenAndUserType(token, userType);
            passwordResetService.updatePassword(userType, entity, password);
            model.addAttribute("message", "Password reset successful! You may now log in.");
            return "login"; // or redirect to login page
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            model.addAttribute("userType", userType);
            return "reset-password";
        }
    }
}
