package com.railbit.TicketManagementSystem.Repository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.railbit.TicketManagementSystem.Entity.Admin;

@Configuration
public class AdminDataLoader {

    @Bean
    public CommandLineRunner dataLoader(AdminRepository repo, PasswordEncoder encoder) {
        return args -> {
            String email = "gagan7870@gmail.com";
            if (repo.findByEmail(email).isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername("Gagan Kumar Nirala");
                admin.setPhone("7870292981");
                admin.setEmail(email);
                admin.setPassword(encoder.encode("gagan@1234"));
                admin.setRole("ROLE_ADMIN");

                repo.save(admin);
                System.out.println("Default admin created.");
            } else {
                System.out.println("Admin already exists. Skipping data load.");
            }
        };
    }
}
