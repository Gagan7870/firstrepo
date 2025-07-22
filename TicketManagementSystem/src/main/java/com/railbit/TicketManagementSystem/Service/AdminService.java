package com.railbit.TicketManagementSystem.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Admin;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.AdminRepository;
import com.railbit.TicketManagementSystem.Repository.CustomerRepository;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;
import com.railbit.TicketManagementSystem.Repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerRepository customerRepository;

    public Admin getAdminByUsername(String email) {
        return adminRepository.findByEmail(email).orElseThrow();
    }

    public List<Tickets> getAllTickets() {
        return ticketRepository.findAll();
    }
    
    public List<User> getAllUsers(){
    	return userRepository.findAll();
    }
    public Page<User> getAllUsersPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void updateTicketStatus(Long ticketId, TicketStatus status) {
        Tickets ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }
    public void updateAdmin(Admin updatedAdmin) {
        Admin existing = adminRepository.findById(updatedAdmin.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        existing.setUsername(updatedAdmin.getUsername());
        existing.setEmail(updatedAdmin.getEmail());
        existing.setPhone(updatedAdmin.getPhone());

        adminRepository.save(existing);
    }
    public boolean checkOldPassword(Admin admin, String oldPassword) {
        return passwordEncoder.matches(oldPassword, admin.getPassword());
    }

    public void updatePassword(Admin admin, String newPassword) {
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalCustomers() {
        return customerRepository.count();
    }

}

