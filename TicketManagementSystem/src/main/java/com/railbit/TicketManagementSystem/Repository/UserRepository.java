package com.railbit.TicketManagementSystem.Repository;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.railbit.TicketManagementSystem.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
   //Optional<User> findByUsername(String username);
   Optional<User> findByEmail(String email);
   Optional<User> findByResetToken(String resetToken);

     
     @Bean
     public default   CommandLineRunner dataLoader(UserRepository repo, PasswordEncoder encoder) {
         return args -> {
             if (repo.findByEmail("email").isEmpty()) {
                 User user = new User();
                 user.setEmail("email");
                 user.setPassword(encoder.encode("password"));
                 user.setRole("ROLE_USER");
                 repo.save(user);
             }
         };
     }
}
