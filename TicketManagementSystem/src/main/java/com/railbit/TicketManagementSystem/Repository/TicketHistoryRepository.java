package com.railbit.TicketManagementSystem.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railbit.TicketManagementSystem.Entity.TicketHistory;
import com.railbit.TicketManagementSystem.Entity.Tickets;

public interface TicketHistoryRepository extends JpaRepository<TicketHistory, Long>{
    
    List<TicketHistory> findByTicketOrderByUpdatedAtDesc(Tickets ticket);

	List<TicketHistory> findByTicket(Tickets ticket);

}
