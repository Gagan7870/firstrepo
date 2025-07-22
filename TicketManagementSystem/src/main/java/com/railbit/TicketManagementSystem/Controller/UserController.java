package com.railbit.TicketManagementSystem.Controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.TicketHistory;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;
import com.railbit.TicketManagementSystem.Service.TicketService;
import com.railbit.TicketManagementSystem.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UserController {

	    @Autowired
	    private UserService userService;
	    @Autowired
	    private TicketService ticketService;
	    @Autowired
	    private TicketRepository ticketRepository;
	    
        @GetMapping("/home")
      public String homePage(Model model) {
       	model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
       	model.addAttribute("role", "Guest"); 
       	return "home";
        }

	    @GetMapping("/register")
	    public String showRegistrationForm(Model model) {
	        model.addAttribute("user", new User());
	        System.out.println("TMS is running fine...."); 
	        return "register";
	    }

	    @PostMapping("/saveUser")
	    public String registerUser(@Valid @ModelAttribute("user") User user,
	    		   BindingResult result, Model model) {
	    	if(result.hasErrors()) {
	    		return "register";
	    	}
	        userService.registerUser(user);
	        return "redirect:/login";
	   }
	    @GetMapping("/login")
	    public String loginPage(@RequestParam(value = "error", required = false) String error,
	                            @RequestParam(value = "logout", required = false) String logout,
	                            Model model) {
	        if (error != null) {
	            model.addAttribute("error", "Invalid User ID or password.");
	        }
	        if (logout != null) {
	            model.addAttribute("message", "You have been logged out successfully.");
	        }
	        return "login";
	    }
	    @GetMapping("/user/dashboard")
	    public String dashboard(Model model, Principal principal) {
	    	
        	model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
        	model.addAttribute("role", "USER");
  	      model.addAttribute("currentPage", "dashboard");

        	model.addAttribute("totalTickets", ticketService.getTotalTickets());
            model.addAttribute("resolvedTickets", ticketService.getResolvedTickets());
            model.addAttribute("pendingTickets", ticketService.getPendingTickets());
	        if (principal != null) {
	        	  String email = principal.getName(); // returns the email (used for login)
	              User user = userService.findByEmail(email); // fetch actual user
	              model.addAttribute("username", user.getUsername());
	              } 
	        else {
	            model.addAttribute("username", "Guest");
	        }
	        return "dashboard";
	    }
	    @GetMapping("/service")
	    public String showServices(Model model) {
	    	model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
        	model.addAttribute("role", "USER");
  	      model.addAttribute("currentPage", "service");

	    	return "service";
	    }
	   

	        @GetMapping("/edit/{id}")
	        public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
	            Tickets ticket = ticketService.getTicketByIdAndUser(id, principal.getName());
                
	            model.addAttribute("ticket", ticket);
	            model.addAttribute("priorities", Priority.values());
	            model.addAttribute("statuses", TicketStatus.values());
	            model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	        	model.addAttribute("role", "USER");
	  	      model.addAttribute("currentPage", "editticket");

	            return "editticket"; 
	        }

	        @PostMapping("/update/{id}")
	        public String updateTicket(@PathVariable Long id, @ModelAttribute Tickets ticket, Principal principal) {

	        	ticketService.updateUserTicket(id, ticket, principal.getName());
	            return "redirect:/statusView";
	        }
	        @PostMapping("/updateStatus/{id}")
	        public String updateTicketStatus(@PathVariable Long id, @RequestParam TicketStatus status, Principal principal) {
	            ticketService.updateTicketStatus(id, status, principal.getName());
	            return "redirect:/statusView";
	        }
	        @PostMapping("/deleteTicket/{id}")
	        public String deleteAssignedTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	            try {
	                ticketService.deleteAssignedTicket(id);
	                redirectAttributes.addFlashAttribute("success", "Ticket deleted successfully.");
	            } catch (EntityNotFoundException e) {
	                redirectAttributes.addFlashAttribute("error", "Ticket not found.");
	            } catch (Exception e) {
	                redirectAttributes.addFlashAttribute("error", "An unexpected error occurred.");
	            }

	            return "redirect:/assignedTickets";
	        }



	        @PostMapping("/delete/{id}")
	        public String deleteTicket(@PathVariable Long id, Principal principal) {
	            ticketService.deleteUserTicket(id, principal.getName());
	            return "redirect:/statusView";
	        }

	        @GetMapping("/assignedTickets")
	        public String showAssignedTickets(@RequestParam(defaultValue = "0") int page,
	                                          @RequestParam(defaultValue = "5") int size,
	                                          Model model,
	                                          Principal principal) {
	            String email = principal.getName();
	            String username = userService.getUsernameByEmail(email);

	            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
	            Page<Tickets> ticketPage = ticketService.getAssignedTicketsPage(username, pageable);

	            model.addAttribute("tickets", ticketPage.getContent());
	            model.addAttribute("totalPages", ticketPage.getTotalPages());
	            model.addAttribute("currentPageIndex", page);
	            model.addAttribute("size", ticketPage.getSize());
	            model.addAttribute("statuses", TicketStatus.values());

	            model.addAttribute("pageTitle", "Assigned Tickets");
	            model.addAttribute("role", "USER");
	            model.addAttribute("currentPage", "assignedTickets");

	            return "assignedTickets";
	        }

	        @PostMapping("/updateAssignedStatus")
	        public String updateAssignedTicketStatus(@RequestParam Long ticketId,
	                                                 @RequestParam TicketStatus status,
	                                                 @RequestParam String remarks,
	                                                 Principal principal,
	                                                 RedirectAttributes redirectAttributes) {
	            String email = principal.getName();
	            String updatedBy = userService.getUsernameByEmail(email); // Get username from email

	            ticketService.updateAssignedTicketStatus(ticketId, status, remarks, updatedBy); // Make sure this accepts remarks

	            redirectAttributes.addFlashAttribute("success", "Ticket status updated successfully.");
	            return "redirect:/assignedTickets";
	        }

	        @GetMapping("/user/ticket-history/{id}")
	        public String getTicketHistory(@PathVariable("id") Long id, Model model) {
	            List<TicketHistory> historyList = ticketService.getTicketHistory(id);
	            model.addAttribute("historyList", historyList);
	            return "fragments/ticketHistory :: historyTable";  // reusing the existing admin fragment
	        }
	        @GetMapping("/user/ticket/{id}/attachment")
	        public ResponseEntity<byte[]> getTicketAttachment(@PathVariable Long id) {
	            Optional<Tickets> optional = ticketRepository.findById(id);
	            if (optional.isEmpty() || optional.get().getAttachment() == null) {
	                return ResponseEntity.notFound().build();
	            }

	            Tickets ticket = optional.get();

	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // or MediaType.PDF, IMAGE, etc.
	            headers.setContentDisposition(ContentDisposition
	                    .inline()
	                    .filename(ticket.getAttachmentFilename())
	                    .build());

	            return new ResponseEntity<>(ticket.getAttachment(), headers, HttpStatus.OK);
	        }
	        @PostMapping("/user/updateStatusWithRemarks")
	        public String updateTicketStatus(@RequestParam Long ticketId,
	                                         @RequestParam String status,
	                                         @RequestParam String remarks,
	                                         Principal principal) {

	            ticketService.updateStatusWithRemarks(ticketId, status, remarks, principal.getName());
	            return "redirect:/assignedTickets"; // your page URL
	        }

}


	


