package com.railbit.TicketManagementSystem.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.railbit.TicketManagementSystem.Entity.TicketLifecycleHistory;
import com.railbit.TicketManagementSystem.Entity.Tickets;

public interface TicketLifecycleHistoryRepository extends JpaRepository<TicketLifecycleHistory, Long> {

    List<TicketLifecycleHistory> findByTicketOrderByUpdatedAtDesc(Tickets ticket);

}
