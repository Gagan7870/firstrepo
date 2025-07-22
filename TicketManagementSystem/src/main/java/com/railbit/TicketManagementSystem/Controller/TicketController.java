package com.railbit.TicketManagementSystem.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.railbit.TicketManagementSystem.Service.CustomerServiceImpl;
import com.railbit.TicketManagementSystem.Service.DepartmentService;
import com.railbit.TicketManagementSystem.Service.TicketService;
import com.railbit.TicketManagementSystem.Service.UserService;

import jakarta.validation.Valid;

@Controller
public class TicketController {
	
	@Autowired
	private TicketService ticketService;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private TicketHistoryRepository ticketHistoryRepository;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	
	
    @GetMapping("/createticket")
    public String dashboard(Model model, Principal principal) {
        List<Tickets> tickets = ticketService.getUserTicket(principal.getName());
        model.addAttribute("tickets", tickets);
        model.addAttribute("tickets", new Tickets()); // for form
        model.addAttribute("departments", departmentService.getAllDepartments());

        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "USER");
	      model.addAttribute("currentPage", "createticket");

    
        return "createticket";
    }

    @PostMapping("/dashboard/create")
    public String createTicket(
            @Valid @ModelAttribute("tickets") Tickets tickets,
            BindingResult result,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttrs,
            Principal principal) {

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
            model.addAttribute("role", "USER");
            model.addAttribute("currentPage", "createticket");
            return "createticket";
        }

        // ✅ Handle file attachment if present
        try {
            if (!file.isEmpty()) {
                tickets.setAttachment(file.getBytes());
                tickets.setAttachmentFilename(file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload attachment.");
            return "createticket";
        }

        ticketService.createTicket(principal.getName(), tickets);
        redirectAttrs.addFlashAttribute("success", "Ticket created successfully!");

        return "redirect:/user/dashboard";
    }

    @GetMapping("/statusView")
    public String statusView(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             Model model, Principal principal) {

        String email = principal.getName();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tickets> ticketPage = ticketService.getUserTickets(email, pageable);

        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("currentPageIndex", ticketPage.getNumber());
        model.addAttribute("ticket", new Tickets());
        model.addAttribute("statuses", TicketStatus.values());

        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
        model.addAttribute("role", "USER");
        model.addAttribute("currentPage", "statusView");

        return "statusView";
    }

    @GetMapping("/customer/customerStatusView")
    public String customerStatusView(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "2") int size,
                                     Model model, Principal principal) {
        String email = principal.getName();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Tickets> ticketPage = ticketService.getUserTickets(email, pageable);

        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("currentPageIndex", page);
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("role", "CUSTOMER");
        model.addAttribute("currentPage", "customerStatusView");

        return "customerStatusView";
    }

    @GetMapping("/contactSupport")
    public String contactSupport(Model model) {
    	model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "USER");
	      model.addAttribute("currentPage", "contactSupport");

    	return "contactSupport";
    }
    @GetMapping("/userUpdate")
    public String showProfileForm(Model model , Principal principal ) {
        User user = ticketService.getUserByUsername(principal.getName()); // fetch current user
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "USER");
	      model.addAttribute("currentPage", "userUpdate");

        return "userUpdate";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, RedirectAttributes redirectAttributes) {    	
        userService.updateUser(updatedUser);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/userUpdate";
    }
 
    @GetMapping("/customerUpdate")
    public String showCustomerProfileForm(Model model , Principal principal ) {
        Customer customer = ticketService.getCustomerByUsername(principal.getName()); // fetch current user
        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("role", "CUSTOMER");
	    model.addAttribute("currentPage", "customerUpdate");

        return "customerUpdate";
    }

    @PostMapping("/customer/updateCustomer")
    public String updateCustomerProfile(@ModelAttribute("customer") Customer updatedCustomer, RedirectAttributes redirectAttributes) {    	
    	customerServiceImpl.updateCustomer(updatedCustomer);

        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/customer/customerDashboard";
    }
 
}
