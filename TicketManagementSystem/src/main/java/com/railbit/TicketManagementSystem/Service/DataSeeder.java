package com.railbit.TicketManagementSystem.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.SLAConfiguration;
import com.railbit.TicketManagementSystem.Repository.SLAConfigurationRepository;


@Component
public class DataSeeder {
    
	   @Autowired
	    private SLAConfigurationRepository slaRepo;
        
	    public void run(String... args) {
	        if (slaRepo.count() == 0) {
	            slaRepo.save(new SLAConfiguration(Priority.HIGH, 4));
	            slaRepo.save(new SLAConfiguration(Priority.MEDIUM, 24));
	            slaRepo.save(new SLAConfiguration(Priority.LOW, 72));
	        }
	    }
}
