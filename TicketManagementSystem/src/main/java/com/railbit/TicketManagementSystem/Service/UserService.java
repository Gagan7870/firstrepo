package com.railbit.TicketManagementSystem.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.UserRepository;

@Service
public class UserService{ 
	
	@Autowired
	private UserRepository userRepository;
	    
	@Autowired
	private PasswordEncoder passwordEncoder;
     
	 public void registerUser(User user) {
	        user.setPassword(passwordEncoder.encode( user.getPassword()));
	        userRepository.save(user);
	    }
	 public User getCurrentUser() {
	        
	        return userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
	    }
	 public void updateUser(User updatedUser) {
	       
		 User existingUser = userRepository.findById(updatedUser.getId())
	                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedUser.getId()));

	        existingUser.setUsername(updatedUser.getUsername());
	        //existingUser.setEmail(updatedUser.getEmail());
	        existingUser.setPhone(updatedUser.getPhone());

	        userRepository.save(existingUser);	
	    }

	   
	    public Optional<User> getUserById(Long id) {
	        return userRepository.findById(id);
	    }

	    
	    public void saveUser(User user) {
	        userRepository.save(user);
	    }

	    
	    public void deleteUser(Long id) {
	        userRepository.deleteById(id);
	    }
	    public User findByEmail(String email) {
	        return userRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	    }
	    
	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }
	    public String getUsernameByEmail(String email) {
	        return userRepository.findByEmail(email)
	            .map(User::getUsername)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	    }

}
