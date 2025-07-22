package com.railbit.TicketManagementSystem.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
        

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole("ROLE_CUSTOMER");
        customerRepo.save(customer);
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));
    }

    @Override
    public Customer getById(Long id) {
        return customerRepo.findById(id).orElse(null);
    }
    public void updateCustomer(Customer updatedCustomer) {
	       
		 Customer existingCustomer = customerRepo.findById(updatedCustomer.getId())
	                .orElseThrow(() -> new RuntimeException("User not found with id: " + updatedCustomer.getId()));

	        existingCustomer.setUsername(updatedCustomer.getUsername());
	        //existingCustomer.setEmail(updatedUser.getEmail());
	        existingCustomer.setPhone(updatedCustomer.getPhone());
	        existingCustomer.setAddress(updatedCustomer.getAddress());

	        customerRepo.save(existingCustomer);	
	    }
    

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void deleteCustomer(Long id) {
    	customerRepo.deleteById(id);
    }

    public void updateCustomers(Customer customer) {
    	customerRepo.save(customer);
    }

	public Page<Customer> getAllCustomersPaginated(Pageable pageable) {
		return customerRepo.findAll(pageable);
	}
}
