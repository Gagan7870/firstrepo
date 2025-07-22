package com.railbit.TicketManagementSystem.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;

public interface TicketServiceinter {
	
    List<Tickets> getAllTickets();
    Optional<Tickets> getTicketById(Long id);
    void saveTicket(Tickets ticket);
    void deleteTicket(Long id);
	void updateTicketStatus(Long id, TicketStatus status,String updatedBy);
	List<Tickets> findByDepartment(Department department);

	    Map<String, Long> getTicketCountByDepartment();
	    Map<String, Long> getTicketCountByUser();
	    Map<String, Long> getSlaBreachReport();
	    Map<String, Long> getDailyTicketStats();
	    Map<String, Long> getWeeklyTicketStats();
}
