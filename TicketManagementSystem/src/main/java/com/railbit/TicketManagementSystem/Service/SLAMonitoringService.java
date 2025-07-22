package com.railbit.TicketManagementSystem.Service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;

@Service
public class SLAMonitoringService {

	 @Autowired
	    private TicketRepository ticketRepository;

	    @Scheduled(fixedRate = 60 * 60 * 1000) // Every hour
	    public void checkSLABreach() {
	        Date now = new Date();
	        List<Tickets> tickets = ticketRepository.findBySlaBreachedFalse();

	        for (Tickets ticket : tickets) {
	            if (ticket.getSlaDueDate() != null && now.after(ticket.getSlaDueDate())) {
	                ticket.setSlaBreached(true);
	                ticketRepository.save(ticket);
	                // Optional: send notification
	            }
	        }
	    }
}
