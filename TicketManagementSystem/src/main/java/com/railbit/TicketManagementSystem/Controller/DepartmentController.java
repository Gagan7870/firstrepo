package com.railbit.TicketManagementSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Service.DepartmentService;
import com.railbit.TicketManagementSystem.Service.TicketService;


@Controller
public class DepartmentController {
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private TicketService ticketService;
	
	
	 @GetMapping("/departments/{name}")
	    public String viewDepartmentTickets(@PathVariable("name") String name, Model model) {
	        Department dept = departmentService.getByName(name)
	            .orElseThrow(() -> new RuntimeException("Department not found: " + name));

	        model.addAttribute("department", dept);
	       // model.addAttribute("tickets", dept.getTickets());
	        model.addAttribute("tickets", ticketService.findByDepartment(dept));

	        model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
  	        model.addAttribute("role", "ADMIN");
  	      model.addAttribute("currentPage", "departmentPage");

	        return "departmentPage";
	    }
	    @PostMapping("/departments/update-status/{id}")
	    public String updateTicketStatus(@PathVariable("id") Long ticketId,
                @RequestParam("status") TicketStatus status) {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        String updatedBy = auth.getName();
	        String departmentName = ticketService.findById(ticketId).getDepartment().getName();
	        ticketService.updateTicketStatus(ticketId, status,updatedBy);

	        return "redirect:/departments/"+ departmentName;
	    }
	    @PostMapping("/admin/tickets/reassign-department")
	    public String reassignTicketToDepartment(@RequestParam Long ticketId,
	                                             @RequestParam Long departmentId,
	                                             RedirectAttributes redirectAttributes) {
	        ticketService.assignDepartmentToTicket(ticketId, departmentId);
	        redirectAttributes.addFlashAttribute("success", "Ticket assigned to department successfully.");
	        return "redirect:/admin/manageTickets";
	    }

}
