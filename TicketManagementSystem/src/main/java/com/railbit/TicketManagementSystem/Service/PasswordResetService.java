package com.railbit.TicketManagementSystem.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Admin;
import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.AdminRepository;
import com.railbit.TicketManagementSystem.Repository.CustomerRepository;
import com.railbit.TicketManagementSystem.Repository.UserRepository;

@Service
public class PasswordResetService {
      

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void processForgotPassword(String email, String appUrl) throws Exception {
        String token = UUID.randomUUID().toString();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setResetToken(token);
            userRepository.save(user);
            sendResetEmail(user.getEmail(), token, "user", appUrl);
            return;
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setResetToken(token);
            customerRepository.save(customer);
            sendResetEmail(customer.getEmail(), token, "customer", appUrl);
            return;
        }

        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setResetToken(token);
            adminRepository.save(admin);
            sendResetEmail(admin.getEmail(), token, "admin", appUrl);
            return;
        }

        throw new Exception("No account found for email: " + email);
    }

    private void sendResetEmail(String email, String token, String userType, String appUrl) {
        String resetUrl = appUrl + "/reset-password?token=" + token + "&userType=" + userType;
        String message = "To reset your password, click the link below:\n" + resetUrl;
        emailService.sendEmail(email, "Password Reset Request", message);
    }

    public Object findByTokenAndUserType(String token, String userType) throws Exception {
        switch(userType.toLowerCase()) {
            case "user":
                return userRepository.findByResetToken(token)
                        .orElseThrow(() -> new Exception("Invalid token."));
            case "customer":
                return customerRepository.findByResetToken(token)
                        .orElseThrow(() -> new Exception("Invalid token."));
            case "admin":
                return adminRepository.findByResetToken(token)
                        .orElseThrow(() -> new Exception("Invalid token."));
            default:
                throw new Exception("Invalid user type.");
        }
    }

    public void updatePassword(String userType, Object entity, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        switch(userType.toLowerCase()) {
            case "user":
                User user = (User) entity;
                user.setPassword(encodedPassword);
                user.setResetToken(null);
                userRepository.save(user);
                break;
            case "customer":
                Customer customer = (Customer) entity;
                customer.setPassword(encodedPassword);
                customer.setResetToken(null);
                customerRepository.save(customer);
                break;
            case "admin":
                Admin admin = (Admin) entity;
                admin.setPassword(encodedPassword);
                admin.setResetToken(null);
                adminRepository.save(admin);
                break;
        }
    }
}
