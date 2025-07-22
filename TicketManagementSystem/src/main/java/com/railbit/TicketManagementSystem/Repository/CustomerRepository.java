package com.railbit.TicketManagementSystem.Repository;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.User;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
  
	Optional<Customer>findByEmail(String email);
    Optional<Customer> findByResetToken(String resetToken);

	 @Bean
	 public default   CommandLineRunner dataLoader(CustomerRepository repo, PasswordEncoder encoder) {
         return args -> {
             if (repo.findByEmail("email").isEmpty()) {
                 Customer customer = new Customer();
                 customer.setEmail("email");
                 customer.setPassword(encoder.encode("password"));
                 customer.setRole("ROLE_CUSTOMER");
                 repo.save(customer);
             }
         };
     }
}
