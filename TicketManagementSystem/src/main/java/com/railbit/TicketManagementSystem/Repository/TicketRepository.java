package com.railbit.TicketManagementSystem.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;

public interface TicketRepository extends JpaRepository<Tickets, Long> {
	Page<Tickets> findByCreatedBy(User user, Pageable pageable);
    Page<Tickets> findByCreatedByCustomer(Customer customer, Pageable pageable); 
    List<Tickets> findBySlaBreachedFalse();
    List<Tickets> findByDepartment(Department department);
    Page<Tickets> findByAssignedToUsername(String username, Pageable pageable);
    List<Tickets> findByCreatedBy(User user);
	List<Tickets> findByCreatedByCustomer(Customer customer);
    
    long count();
    long countByStatus(TicketStatus status);

    
    @Query("SELECT t FROM Tickets t " +
    	       "LEFT JOIN t.createdBy u " +
    	       "LEFT JOIN t.createdByCustomer c " +
    	       "WHERE (:status IS NULL OR t.status = :status) " +
    	       "AND (:priority IS NULL OR t.priority = :priority) " +
    	       "AND (" +
    	       "  :username IS NULL OR " +
    	       "  LOWER(COALESCE(u.username, c.username)) LIKE LOWER(CONCAT('%', :username, '%'))" +
    	       ") " +
    	       "AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
    	       "  OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
    	       "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
    	       "AND (:endDate IS NULL OR t.createdAt <= :endDate)")
    	List<Tickets> filterTickets(
    	    @Param("status") TicketStatus status,
    	    @Param("priority") Priority priority,
    	    @Param("username") String username,
    	    @Param("keyword") String keyword,
    	    @Param("startDate") LocalDateTime startDate,
    	    @Param("endDate") LocalDateTime endDate
    	);
	


}