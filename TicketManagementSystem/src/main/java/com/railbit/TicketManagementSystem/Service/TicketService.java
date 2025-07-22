package com.railbit.TicketManagementSystem.Service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Entity.LifecycleStage;
import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.SLAConfiguration;
import com.railbit.TicketManagementSystem.Entity.TicketHistory;
import com.railbit.TicketManagementSystem.Entity.TicketLifecycleHistory;
import com.railbit.TicketManagementSystem.Entity.TicketSlaInfo;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.AdminRepository;
import com.railbit.TicketManagementSystem.Repository.CustomerRepository;
import com.railbit.TicketManagementSystem.Repository.DepartmentRepository;
import com.railbit.TicketManagementSystem.Repository.SLAConfigurationRepository;
import com.railbit.TicketManagementSystem.Repository.TicketHistoryRepository;
import com.railbit.TicketManagementSystem.Repository.TicketLifecycleHistoryRepository;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;
import com.railbit.TicketManagementSystem.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@Service
public class TicketService implements TicketServiceinter{
    
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SLAConfigurationRepository slaRepo;
	@Autowired
    private DepartmentRepository departmentRepository;
	@Autowired
	private TicketHistoryRepository ticketHistoryRepository;
	@Autowired
	private TicketLifecycleHistoryRepository ticketLifecycleHistoryRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AdminRepository adminRepository;
	
    public Tickets createTicket(String email, Tickets ticket) {
        Optional <User> userOpt = userRepository.findByEmail(email);
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (userOpt.isPresent()) {
         User user = userOpt.get();

        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setUser(user);
        }
     else if (customerOpt.isPresent()) {
        Customer customer = customerOpt.get();
        ticket.setCreatedByCustomer(customer); 
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCustomer(customer);
    } else {
        throw new UsernameNotFoundException("No user or customer found with email: " + email);
    }

        ticket.setCreatedAt(LocalDateTime.now());
        SLAConfiguration sla = slaRepo.findByPriority(ticket.getPriority());

        if (sla != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Date.from(ticket.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
            cal.add(Calendar.HOUR, sla.getResolutionHours());
            ticket.setSlaDueDate(cal.getTime());
        }

        ticket.setSlaBreached(false); 
        return ticketRepository.save(ticket);
    }

    public Page<Tickets> getUserTickets(String email, Pageable pageable) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            return ticketRepository.findByCreatedBy(userOpt.get(), pageable);
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            return ticketRepository.findByCreatedByCustomer(customerOpt.get(), pageable);
        }

        throw new UsernameNotFoundException("No user or customer found with email: " + email);
    }
    public List<Tickets> getUserTicket(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            return ticketRepository.findByCreatedBy(userOpt.get());
        }

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            return ticketRepository.findByCreatedByCustomer(customerOpt.get());
        }

        throw new UsernameNotFoundException("No user or customer found with email: " + email);
    }
    public User getUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public Customer getCustomerByUsername(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }

      @Override
      public List<Tickets> getAllTickets() {
	
	    return ticketRepository.findAll();
     }

      @Override
      public Optional<Tickets> getTicketById(Long id) {
	     return ticketRepository.findById(id);
     }
      

      @Override
      public void saveTicket(Tickets ticket) {
    	 ticketRepository.save(ticket);
     }
      public Tickets getTicketByIdAndUser(Long ticketId, String email) {
    	    User user = userRepository.findByEmail(email)
    	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    	    return ticketRepository.findById(ticketId)
    	            .filter(ticket -> ticket.getCreatedBy().equals(user))
    	            .orElseThrow(() -> new RuntimeException("Ticket not found or access denied"));
    	} 
      public Tickets getTicketByIdAndCustomer(Long ticketId, String email) {
  	    Customer customer = customerRepository.findByEmail(email)
  	            .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
  	    return ticketRepository.findById(ticketId)
  	            .filter(ticket -> ticket.getCreatedByCustomer().equals(customer))
  	            .orElseThrow(() -> new RuntimeException("Ticket not found or access denied"));
  	} 
      
      public Tickets updateUserTicket(Long ticketId, Tickets updatedTicket, String email) {
            Tickets ticket = getTicketByIdAndUser(ticketId, email);
            ticket.setTitle(updatedTicket.getTitle());
            ticket.setDescription(updatedTicket.getDescription());
            ticket.setPriority(updatedTicket.getPriority());
            ticket.setStatus(updatedTicket.getStatus());
            return ticketRepository.save(ticket);
        }

        public void deleteUserTicket(Long ticketId, String email) {
            Tickets ticket = getTicketByIdAndUser(ticketId, email);
            ticketRepository.delete(ticket);
        }
        public void deleteCustomerTicket(Long ticketId, String email) {
            Tickets ticket = getTicketByIdAndCustomer(ticketId, email);
            ticketRepository.delete(ticket);
        }
      public void updateTicket(Tickets updatedTicket, String userEmail) {
    	    Tickets existing = ticketRepository.findById(updatedTicket.getId())
    	            .orElseThrow(() -> new RuntimeException("Ticket not found"));

    	    if (!existing.getUser().getEmail().equals(userEmail)) {
    	        throw new RuntimeException("Unauthorized update attempt.");
    	    }

    	    existing.setTitle(updatedTicket.getTitle());
    	    existing.setDescription(updatedTicket.getDescription());
    	    existing.setPriority(updatedTicket.getPriority());
    	    existing.setStatus(updatedTicket.getStatus());

    	    ticketRepository.save(existing);
    	}

//      public void updateCustomerTicket(Tickets updatedTicket, String customerEmail) {
//  	    Tickets existing = ticketRepository.findById(updatedTicket.getId())
//  	            .orElseThrow(() -> new RuntimeException("Ticket not found"));
//
//  	    if (!existing.getCustomer().getEmail().equals(customerEmail)) {
//  	        throw new RuntimeException("Unauthorized update attempt.");
//  	    }
//
//  	    existing.setTitle(updatedTicket.getTitle());
//  	    existing.setDescription(updatedTicket.getDescription());
//  	    existing.setPriority(updatedTicket.getPriority());
//  	    existing.setStatus(updatedTicket.getStatus());
//  	    
//
//  	    ticketRepository.save(existing);
//  	}
      public Tickets updateCustomerTicket(Long ticketId, Tickets updatedTicket, String email) {
          Tickets ticket = getTicketByIdAndCustomer(ticketId, email);
          ticket.setTitle(updatedTicket.getTitle());
          ticket.setDescription(updatedTicket.getDescription());
          ticket.setPriority(updatedTicket.getPriority());
          ticket.setStatus(updatedTicket.getStatus());
          return ticketRepository.save(ticket);
      }
//      @Override
//      public void deleteTicket(Long id) {
//    	  ticketRepository.deleteById(id);
//     }
      @Transactional
      public void deleteTicket(Long ticketId) {
          Tickets ticket = ticketRepository.findById(ticketId)
              .orElseThrow(() -> new RuntimeException("Ticket not found"));

          // Delete ticket through entity reference to allow cascading to work
          ticketRepository.delete(ticket);
      }

      @Override
      public void updateTicketStatus(Long id, TicketStatus status, String updatedBy) {
          ticketRepository.findById(id).ifPresent(tickets -> {
              TicketStatus oldStatus = tickets.getStatus();

              tickets.setStatus(status);
              ticketRepository.save(tickets);
              
              logTicketHistory(tickets, "STATUS_UPDATED", 
                      "Status changed from " + oldStatus + " to " + status, updatedBy);
          });
      }
//      public List<Tickets> filterTickets(TicketStatus status, Priority priority, String username,
//              String keyword, LocalDateTime startDate, LocalDateTime endDate) {
//    if (keyword != null && keyword.trim().isEmpty()) keyword = null;
//    if (username != null && username.trim().isEmpty()) username = null;
//       return ticketRepository.filterTickets(status, priority, username, keyword, startDate, endDate);
//      }
      public List<Tickets> filterTickets(TicketStatus status, Priority priority, String username,
              String keyword, LocalDateTime startDate, LocalDateTime endDate) {
if (username != null && username.trim().isEmpty()) {
username = null;
}
if (keyword != null && keyword.trim().isEmpty()) {
keyword = null;
}

return ticketRepository.filterTickets(status, priority, username, keyword, startDate, endDate);
}

      public boolean isSlaBreached(Tickets ticket) {
    	    if (ticket.getPriority() == null || ticket.getCreatedAt() == null) return false;

    	    Duration slaDuration;
    	    switch (ticket.getPriority()) {
    	        case HIGH:
    	            slaDuration = Duration.ofHours(4);
    	            break;
    	        case MEDIUM:
    	            slaDuration = Duration.ofHours(12);
    	            break;
    	        case LOW:
    	            slaDuration = Duration.ofHours(24);
    	            break;
    	        default:
    	            return false;
    	    }

    	    LocalDateTime slaDue = ticket.getCreatedAt().plus(slaDuration);
    	    return LocalDateTime.now().isAfter(slaDue) && ticket.getStatus() != TicketStatus.RESOLVED;
    	}
      
      public LocalDateTime getSlaDue(Tickets ticket) {
    	    Duration slaDuration = switch (ticket.getPriority()) {
    	        case HIGH -> Duration.ofHours(4);
    	        case MEDIUM -> Duration.ofHours(12);
    	        case LOW -> Duration.ofHours(24);
    	        default -> Duration.ofHours(24);
    	    };
    	    return ticket.getCreatedAt().plus(slaDuration);
    	}
      

      public void assignDepartmentToTicket(Long ticketId, Long departmentId) {
          Tickets ticket = ticketRepository.findById(ticketId)
              .orElseThrow(() -> new RuntimeException("Ticket not found"));

          Department department = departmentRepository.findById(departmentId)
              .orElseThrow(() -> new RuntimeException("Department not found"));

          ticket.setDepartment(department);
          ticketRepository.save(ticket);
      }

      public long getTotalTickets() {
          return ticketRepository.count();
      }

      public long getResolvedTickets() {
          return ticketRepository.countByStatus(TicketStatus.RESOLVED);
      }

      public long getPendingTickets() {
          return ticketRepository.countByStatus(TicketStatus.OPEN) +
        		 ticketRepository.countByStatus(TicketStatus.ON_HOLD) +
                 ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
      }
      
      public void logTicketHistory(Tickets ticket, String action, String description, String updatedBy) {
    	    TicketHistory history = new TicketHistory();
    	    history.setTicket(ticket);
    	    history.setAction(action);
    	    history.setDescription(description);
            history.setUpdatedBy(updatedBy);
    	    
    	    history.setUpdatedAt(LocalDateTime.now()); // ✅ This line is essential
    	    ticketHistoryRepository.save(history);
    	}

      public List<Tickets> getAllTicketsWithHistory() {
    	    List<Tickets> tickets = ticketRepository.findAll();
    	    for (Tickets ticket : tickets) {
    	        List<TicketHistory> historyList = ticketHistoryRepository.findByTicket(ticket);
    	        ticket.setHistoryList(historyList); // ← add this method to entity
    	    }
    	    return tickets;
    	}

	public Tickets findById(Long ticketId) {
		 return ticketRepository.findById(ticketId)
		           .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
	}

	public void save(Tickets ticket) {
	    ticketRepository.save(ticket);
}
	
	public void updateLifecycleStage(Long ticketId, LifecycleStage newStage, String updatedBy) {
	    Tickets ticket = ticketRepository.findById(ticketId)
	        .orElseThrow(() -> new RuntimeException("Ticket not found"));

	    LifecycleStage oldStage = ticket.getLifecycleStage();
	    ticket.setLifecycleStage(newStage);
	    ticketRepository.save(ticket);

	    TicketLifecycleHistory history = new TicketLifecycleHistory();
	    history.setTicket(ticket);
	    history.setFromStage(oldStage);
	    history.setToStage(newStage);
	    history.setUpdatedAt(LocalDateTime.now());
	    history.setUpdatedBy(updatedBy);

	    ticketLifecycleHistoryRepository.save(history);
	}
	@Override
	public List<Tickets> findByDepartment(Department department) {
	    return ticketRepository.findByDepartment(department);
	}
    
	  @Override
	    public Map<String, Long> getTicketCountByDepartment() {
	        List<Tickets> tickets = ticketRepository.findAll();
	        return tickets.stream()
	            .filter(t -> t.getDepartment() != null)
	            .collect(Collectors.groupingBy(t -> t.getDepartment().getName(), Collectors.counting()));
	    }

	  @Override
	    public Map<String, Long> getTicketCountByUser() {
	        List<Tickets> tickets = ticketRepository.findAll();
	        return tickets.stream()
	            .filter(t -> t.getUser() != null)
	            .collect(Collectors.groupingBy(t -> t.getUser().getUsername(), Collectors.counting()));
	    }

	  @Override
	    public Map<String, Long> getSlaBreachReport() {
	        List<Tickets> tickets = ticketRepository.findAll();
	        Map<String, Long> slaReport = new HashMap<>();
	        slaReport.put("Breached", tickets.stream().filter(Tickets::isSlaBreached).count());
	        slaReport.put("On Time", tickets.stream().filter(t -> !t.isSlaBreached()).count());
	        return slaReport;
	    }

	    @Override
	    public Map<String, Long> getDailyTicketStats() {
	        List<Tickets> tickets = ticketRepository.findAll();
	        return tickets.stream()
	            .collect(Collectors.groupingBy(
	                t -> t.getCreatedAt().toLocalDate().toString(),
	                TreeMap::new,
	                Collectors.counting()
	            ));
	    }

	    @Override
	    public Map<String, Long> getWeeklyTicketStats() {
	        List<Tickets> tickets = ticketRepository.findAll();
	        Map<String, Long> weeklyStats = new TreeMap<>();

	        for (Tickets ticket : tickets) {
	            LocalDate createdDate = ticket.getCreatedAt().toLocalDate();
	            int weekOfYear = createdDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
	            String label = "Week " + weekOfYear;
	            weeklyStats.put(label, weeklyStats.getOrDefault(label, 0L) + 1);
	        }
	        return weeklyStats;
	    }
	    public void assignTicketToUser(Long ticketId, Long userId) {
	        Tickets ticket = ticketRepository.findById(ticketId)
	            .orElseThrow(() -> new RuntimeException("Ticket not found"));

	        User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        ticket.setAssignedTo(user);
	        ticketRepository.save(ticket);
	    }
	    public Page<Tickets> getAssignedTicketsPage(String username, Pageable pageable) {
	        return ticketRepository.findByAssignedToUsername(username, pageable);
	    }

	    @Transactional
	    public void updateAssignedTicketStatus(Long ticketId, TicketStatus status, String remarks, String updatedBy) {
	        Tickets ticket = ticketRepository.findById(ticketId)
	                .orElseThrow(() -> new RuntimeException("Ticket not found"));

	        if (ticket.getAssignedTo() == null || !ticket.getAssignedTo().getUsername().equals(updatedBy)) {
	            throw new RuntimeException("Unauthorized status update.");
	        }

	        ticket.setStatus(status);
	        ticketRepository.save(ticket);
	        
	        logTicketHistory(
	                  ticket,
	                  "Status Updated to " + status,
	                  remarks, updatedBy
	                  
	          );
	        }
	    
	    public void deleteAssignedTicket(Long ticketId) {
	        Tickets ticket = ticketRepository.findById(ticketId)
	            .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

	        ticketRepository.delete(ticket);
	    }


	    public List<TicketHistory> getTicketHistory(Long ticketId) {
	        Tickets ticket = ticketRepository.findById(ticketId)
	                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
	        
	        return ticketHistoryRepository.findByTicketOrderByUpdatedAtDesc(ticket);
	    }
	    
	    public Page<TicketSlaInfo> getPaginatedSlaTickets(int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);

	        Page<Tickets> pagedTickets = ticketRepository.findAll(pageable);

	        List<TicketSlaInfo> ticketInfoList = pagedTickets
	            .stream()
	            .filter(ticket -> ticket.getPriority() != null && ticket.getCreatedAt() != null)
	            .map(ticket -> {
	                LocalDateTime slaDue = getSlaDue(ticket);
	                boolean breached = isSlaBreached(ticket);
	                return new TicketSlaInfo(ticket, slaDue, breached);
	            }).collect(Collectors.toList());

	        return new PageImpl<>(ticketInfoList, pageable, ticketRepository.count());
	    }
	   
	    public Page<Tickets> getAllTicketsPaginated(int page, int size) {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
	        return ticketRepository.findAll(pageable);
	    }
	    public void updateAllAssignmentDetails(Long ticketId, String status, String lifecycleStage,
                Long userId, Long departmentId, String updatedBy,String description) {
            Tickets ticket = ticketRepository.findById(ticketId).orElseThrow();

            LifecycleStage oldStage = ticket.getLifecycleStage();
            LifecycleStage newStage = LifecycleStage.valueOf(lifecycleStage);
            
            TicketStatus oldStatus = ticket.getStatus();
            TicketStatus newStatus = TicketStatus.valueOf(status);

ticket.setStatus(TicketStatus.valueOf(status));
ticket.setLifecycleStage(newStage);
ticket.setAssignedTo(userRepository.findById(userId).orElse(null));
ticket.setDepartment(departmentRepository.findById(departmentId).orElse(null));
ticketRepository.save(ticket);

if (oldStage != newStage) {
TicketLifecycleHistory history = new TicketLifecycleHistory();
history.setTicket(ticket);
history.setFromStage(oldStage);
history.setToStage(newStage);
history.setUpdatedAt(LocalDateTime.now());
history.setUpdatedBy(updatedBy);
ticketLifecycleHistoryRepository.save(history);
}

TicketHistory ticketHistory = new TicketHistory();
ticketHistory.setTicket(ticket);
ticketHistory.setAction("Status Updated to " + status); 
ticketHistory.setDescription(description); // The remarks
ticketHistory.setUpdatedAt(LocalDateTime.now());
ticketHistory.setUpdatedBy(updatedBy);
ticketHistoryRepository.save(ticketHistory);
}
	    public void updateStatusWithRemarks(Long ticketId, String newStatus, String remarks, String updatedBy) {
	        Tickets ticket = ticketRepository.findById(ticketId).orElseThrow();

	        TicketStatus oldStatus = ticket.getStatus();
	        TicketStatus newStat = TicketStatus.valueOf(newStatus);

	        if (oldStatus != newStat) {
	            ticket.setStatus(newStat);
	            ticketRepository.save(ticket);

	            TicketHistory history = new TicketHistory();
	            history.setTicket(ticket);
	            history.setAction("Status Updated to " + newStatus);
	            history.setDescription(remarks);
	            history.setUpdatedBy(updatedBy);
	            history.setUpdatedAt(LocalDateTime.now());
	            ticketHistoryRepository.save(history);
	        }
	    }

}


