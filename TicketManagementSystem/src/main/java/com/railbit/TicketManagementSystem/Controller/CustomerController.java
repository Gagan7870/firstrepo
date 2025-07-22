package com.railbit.TicketManagementSystem.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.TicketHistory;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.TicketHistoryRepository;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;
import com.railbit.TicketManagementSystem.Service.CustomerService;
import com.railbit.TicketManagementSystem.Service.DepartmentService;
import com.railbit.TicketManagementSystem.Service.TicketService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private TicketRepository ticketRepository; 
	
	@Autowired
	private TicketHistoryRepository ticketHistoryRepository;
     
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
	    model.addAttribute("currentPage", "registerCustomer");

        System.out.println("TMS is running fine...."); 
        return "registerCustomer";
    }

    @PostMapping("/saveCustomer")
    public String registerUser(@Valid @ModelAttribute("customer") Customer customer,
    		   BindingResult result, Model model) {
    	if(result.hasErrors()) {
    		return "registerCustomer";
    	}
        customerService.register(customer);
        return "redirect:/login";
   }
    
    @GetMapping("/customerDashboard")
    public String dashboard(Model model, Principal principal) {
    	 if (principal != null) {
       	  String email = principal.getName(); // returns the email (used for login)
             Customer customer = customerService.findByEmail(email);
             model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
   	         model.addAttribute("role", "CUSTOMER");
   	         model.addAttribute("currentPage", "customerDashboard");// fetch actual user
             model.addAttribute("username", customer.getUsername());
             
             } 
       else {
           model.addAttribute("username", "Guest");
       }
        return "customerDashboard";
    }
    @GetMapping("/createticket")
    public String dashboard1(Model model, Principal principal) {
        List<Tickets> tickets = ticketService.getUserTicket(principal.getName());
        model.addAttribute("tickets", tickets);
        model.addAttribute("tickets", new Tickets()); // for form
        model.addAttribute("departments", departmentService.getAllDepartments());

        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "CUSTOMER");
	      model.addAttribute("currentPage", "cuscreateticket");

    
        return "cuscreateticket";
    }

    @PostMapping("/dashboard/create")
    public String createTicket(
            @Valid @ModelAttribute("tickets") Tickets tickets,
            BindingResult result,
            @RequestParam (value = "file", required = false) MultipartFile file,  // <== add this
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs,
            Principal principal) throws IOException {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
            model.addAttribute("role", "USER");
            model.addAttribute("currentPage", "createticket");

            return "cuscreateticket"; // return same form page if validation fails
        }

        if (!file.isEmpty()) {
            tickets.setAttachment(file.getBytes());
            tickets.setAttachmentFilename(file.getOriginalFilename());
        }

        ticketService.createTicket(principal.getName(), tickets);
        redirectAttrs.addFlashAttribute("success", "Ticket created successfully!");

        return "redirect:/customer/customerDashboard";
    }

    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Tickets ticket = ticketService.getTicketByIdAndCustomer(id, principal.getName());
        
        model.addAttribute("ticket", ticket);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "CUSTOMER");
	      model.addAttribute("currentPage", "customereditticket");

        return "customereditticket"; 
    }

    @PostMapping("/update/{id}")
    public String updatingCustomerTicket(@PathVariable Long id, @ModelAttribute Tickets ticket, Principal principal) {

    	ticketService.updateCustomerTicket(id, ticket, principal.getName());
        return "redirect:/customer/customerStatusView";
    }
  
    @GetMapping("/ticket/{id}/attachment")
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


    @PostMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id, Principal principal) {
        ticketService.deleteCustomerTicket(id, principal.getName());
        return "redirect:/customer/customerStatusView";
    }
    
    @GetMapping("/ticket-history/{ticketId}")
    public String getTicketHistoryFragment(@PathVariable Long ticketId, Model model) {
       Tickets ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));

       List<TicketHistory> historyList = ticketHistoryRepository.findByTicketOrderByUpdatedAtDesc(ticket);
        model.addAttribute("ticket", ticket);
        model.addAttribute("historyList", historyList);
        return "fragments/ticketHistory :: historyTable";
    }
}
