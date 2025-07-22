package com.railbit.TicketManagementSystem.Service;

import com.railbit.TicketManagementSystem.Entity.Customer;

public interface CustomerService {
       
          void register(Customer customer);
          Customer findByEmail(String email);
          Customer getById(Long id);
}
