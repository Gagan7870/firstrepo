package com.railbit.TicketManagementSystem.Service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Admin;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Entity.Customer;

import com.railbit.TicketManagementSystem.Repository.AdminRepository;
import com.railbit.TicketManagementSystem.Repository.CustomerRepository;
import com.railbit.TicketManagementSystem.Repository.UserRepository;
@Service
public class CustomAdminDetailService implements UserDetailsService {

	    @Autowired
	    private AdminRepository adminRepository;
	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private CustomerRepository customerRepository;

	    @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            
	    	Optional<User> userOpt = userRepository.findByEmail(email);
	        if (userOpt.isPresent()) {
	            User user = userOpt.get();
	            return new org.springframework.security.core.userdetails.User(
	                    user.getEmail(), 
	                    user.getPassword(),
	                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
	            );
	        }
	        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
	        if (customerOpt.isPresent()) {
	            Customer customer = customerOpt.get();
	            return new org.springframework.security.core.userdetails.User(
	                    customer.getEmail(), 
	                    customer.getPassword(),
	                    Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
	            );
	        }
	        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
	        if (adminOpt.isPresent()) {
	            Admin admin = adminOpt.get();
	            return new org.springframework.security.core.userdetails.User(
	                admin.getEmail(),
	                admin.getPassword(),
	                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
	            );
	        }

	        throw new UsernameNotFoundException("User not found with username: " + email);
	    }

	}


