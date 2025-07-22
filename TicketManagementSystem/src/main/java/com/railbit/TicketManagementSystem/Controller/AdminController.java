package com.railbit.TicketManagementSystem.Controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railbit.TicketManagementSystem.Entity.Admin;
import com.railbit.TicketManagementSystem.Entity.ChangePassword;
import com.railbit.TicketManagementSystem.Entity.Customer;
import com.railbit.TicketManagementSystem.Entity.Department;
import com.railbit.TicketManagementSystem.Entity.LifecycleStage;
import com.railbit.TicketManagementSystem.Entity.Priority;
import com.railbit.TicketManagementSystem.Entity.TicketHistory;
import com.railbit.TicketManagementSystem.Entity.TicketLifecycleHistory;
import com.railbit.TicketManagementSystem.Entity.TicketSlaInfo;
import com.railbit.TicketManagementSystem.Entity.TicketStatus;
import com.railbit.TicketManagementSystem.Entity.Tickets;
import com.railbit.TicketManagementSystem.Entity.User;
import com.railbit.TicketManagementSystem.Repository.AdminRepository;
import com.railbit.TicketManagementSystem.Repository.TicketHistoryRepository;
import com.railbit.TicketManagementSystem.Repository.TicketLifecycleHistoryRepository;
import com.railbit.TicketManagementSystem.Repository.TicketRepository;
import com.railbit.TicketManagementSystem.Service.AdminService;
import com.railbit.TicketManagementSystem.Service.CustomerServiceImpl;
import com.railbit.TicketManagementSystem.Service.DepartmentService;
import com.railbit.TicketManagementSystem.Service.TicketService;
import com.railbit.TicketManagementSystem.Service.TicketServiceinter;
import com.railbit.TicketManagementSystem.Service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private AdminService adminService;
	@Autowired
	private TicketService ticketService;
	@Autowired
	private TicketRepository ticketRepository; 
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private TicketHistoryRepository ticketHistoryRepository;
	@Autowired
	private TicketLifecycleHistoryRepository ticketLifecycleHistoryRepository;
	@Autowired
	private TicketServiceinter ticketServiceinter;
	@Autowired
	private CustomerServiceImpl customerServiceImpl;
	
	
	  @GetMapping("/dashboard")
	    public String dashboard(Model model, Principal principal) throws JsonProcessingException {
      	model.addAttribute("role", "ADMIN");
    	model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	model.addAttribute("currentPage", "adminDashboard");
    	model.addAttribute("totalUsers", adminService.getTotalUsers());
        model.addAttribute("totalCustomers", adminService.getTotalCustomers());
    	model.addAttribute("totalTickets", ticketService.getTotalTickets());
        model.addAttribute("resolvedTickets", ticketService.getResolvedTickets());
        model.addAttribute("pendingTickets", ticketService.getPendingTickets());
        
        ObjectMapper objectMapper = new ObjectMapper();
        model.addAttribute("ticketsByDepartment", objectMapper.writeValueAsString(ticketServiceinter.getTicketCountByDepartment()));
        model.addAttribute("ticketsByUser", objectMapper.writeValueAsString(ticketServiceinter.getTicketCountByUser()));
        model.addAttribute("slaReport", objectMapper.writeValueAsString(ticketServiceinter.getSlaBreachReport()));
        model.addAttribute("dailyTicketStats", objectMapper.writeValueAsString(ticketServiceinter.getDailyTicketStats()));
        model.addAttribute("weeklyTicketStats", objectMapper.writeValueAsString(ticketServiceinter.getWeeklyTicketStats()));


        model.addAttribute("isAdminPage", true);

	        if (principal != null) {
	            model.addAttribute("username", principal.getName());
	        } else {
	            model.addAttribute("username", "Guest");
	        }
	        return "adminDashboard";
	    }
	  
	  @GetMapping("/manageusers")
	  public String listUsers(@RequestParam(defaultValue = "0") int page,
	                          @RequestParam(defaultValue = "3") int size,
	                          Model model) {

		    Pageable pageable = PageRequest.of(page, size);
	      Page<User> userPage = adminService.getAllUsersPaginated(pageable);

	      model.addAttribute("users", userPage.getContent());
	      model.addAttribute("totalPages", userPage.getTotalPages());
	      model.addAttribute("size", size); // ← important!

	      model.addAttribute("currentPageIndex", page);

	      model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	      model.addAttribute("role", "ADMIN");
	      model.addAttribute("currentPage", "manageusers");

	      return "manageusers";
	  }


	  
	  @GetMapping("/users/edit/{id}")
	  public String editUser(@PathVariable Long id, Model model) {
	      return userService.getUserById(id)
	          .map(user -> {
	              model.addAttribute("user", user);
	              model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	    	      model.addAttribute("role", "ADMIN");
	    	      model.addAttribute("currentPage", "editUser");

	              return "editUser";
	          })
	          .orElse("redirect:/admin/manageusers");
	  }

	  @PostMapping("/users/update")
	  public String updateUser(@ModelAttribute("user") User user) {
	      userService.saveUser(user);
	      return "redirect:/admin/manageusers";
	  }

	  @GetMapping("/users/delete/{id}")
	  public String deleteUser(@PathVariable Long id) {
	      userService.deleteUser(id);
	      return "redirect:/admin/manageusers";
	  }
	  
//	  @GetMapping("/manageTickets")
//	  public String filterTickets(
//	          @RequestParam(required = false) TicketStatus status,
//	          @RequestParam(required = false) Priority priority,
//	          @RequestParam(required = false) String username,
//	          @RequestParam(required = false) String keyword,
//	          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//	          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//	          Model model) {
//
//	      List<Tickets> tickets = ticketService.filterTickets(status, priority, username, keyword, startDate, endDate);
//	      List<User> users = userService.getAllUsers();
//	      users.forEach(u -> System.out.println("Loaded user: " + u.getUsername() ));
//
//	      model.addAttribute("users", users); 
//	      model.addAttribute("departments", departmentService.getAllDepartments());
//	      model.addAttribute("tickets", tickets);
//	      model.addAttribute("statuses", TicketStatus.values());
//	      model.addAttribute("priorities", Priority.values());
//	      model.addAttribute("selectedStatus", status);
//	      model.addAttribute("selectedPriority", priority);
//	      model.addAttribute("username", username);
//	      model.addAttribute("keyword", keyword);
//	      model.addAttribute("startDate", startDate);
//	      model.addAttribute("endDate", endDate);
//	      model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
//	      model.addAttribute("role", "ADMIN");
//	      model.addAttribute("currentPage", "manageTickets");
//
//	      return "manageTickets";
//	  }
	  @GetMapping("/manageTickets")
	  public String filterTickets(
	          @RequestParam(defaultValue = "0") int page,
	          @RequestParam(defaultValue = "8") int size,
	          @RequestParam(required = false) TicketStatus status,
	          @RequestParam(required = false) Priority priority,
	          @RequestParam(required = false) String username,
	          @RequestParam(required = false) String keyword,
	          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
	          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
	          Model model) {

	      List<User> users = userService.getAllUsers();
	      users.forEach(u -> System.out.println("Loaded user: " + u.getUsername()));

	      List<Tickets> tickets;

	      // If any filter is applied, use filtered tickets (no pagination)
	      if (status != null || priority != null || username != null || keyword != null || startDate != null || endDate != null) {
	          tickets = ticketService.filterTickets(status, priority, username, keyword, startDate, endDate);
	          model.addAttribute("tickets", tickets);
	          model.addAttribute("pageIndex", 0);
	          model.addAttribute("totalPages", 1);  // No pagination for filtered
	      } else {
	          // Otherwise use paginated results
	          Page<Tickets> ticketsPage = ticketService.getAllTicketsPaginated(page, size);
	          model.addAttribute("tickets", ticketsPage.getContent());
	          model.addAttribute("pageIndex", page);
	          model.addAttribute("totalPages", ticketsPage.getTotalPages());
	      }

	      model.addAttribute("users", users);
	      model.addAttribute("departments", departmentService.getAllDepartments());
	      model.addAttribute("statuses", TicketStatus.values());
	      model.addAttribute("priorities", Priority.values());
	      model.addAttribute("selectedStatus", status);
	      model.addAttribute("selectedPriority", priority);
	      model.addAttribute("username", username);
	      model.addAttribute("keyword", keyword);
	      model.addAttribute("startDate", startDate);
	      model.addAttribute("endDate", endDate);
	      model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	      model.addAttribute("role", "ADMIN");
	      model.addAttribute("currentPage", "manageTickets");

	      return "manageTickets";
	  }


	  @PostMapping("/tickets/delete/{id}")
	  public String deleteTicket(@PathVariable Long id) {
	      ticketService.deleteTicket(id);
	      return "redirect:/admin/manageTickets";
	  }
//	  @PostMapping("/tickets/updateStatus")
//	  public String updateTicketStatus( @RequestParam("ticketId") Long ticketId,
//	                                   @RequestParam("status") TicketStatus status, 
//	                                    Principal principal) {
//		    String updatedBy = principal.getName(); 
//
//	      ticketService.updateTicketStatus(ticketId, status,updatedBy);
//	      return "redirect:/admin/manageTickets";
//	  }




      @GetMapping("/profile")
      public String showProfile(Model model ,Principal principal) {
    	  Admin admin=adminService.getAdminByUsername(principal.getName());
    	     model.addAttribute("admin",admin);
    	     model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
         	 model.addAttribute("role", "ADMIN");
   	      model.addAttribute("currentPage", "profileSetting");

    	     return "profileSetting";
      }
      
      @PostMapping("/updateProfile")
      public String updateProfile(@ModelAttribute("admin") Admin updatedAdmin,
                                  RedirectAttributes redirectAttributes) {
          adminService.updateAdmin(updatedAdmin);
          redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
          return "redirect:/admin/profile";
      }
      @GetMapping("/changePassword")
      public String showChangePasswordForm(Model model) {
          model.addAttribute("changePassword", new ChangePassword());
          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
      	  model.addAttribute("role", "ADMIN");
          return "adminChangePassword";
      }

      @PostMapping("/changePassword")
      public String changePassword(@ModelAttribute("changePassword") ChangePassword form,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
          Admin admin = adminService.getAdminByUsername(principal.getName());

          if (!adminService.checkOldPassword(admin, form.getOldPassword())) {
              redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
              return "redirect:/admin/changePassword";
          }

          if (!form.getNewPassword().equals(form.getConfirmpassword())) {
              redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
              return "redirect:/admin/changePassword";
          }

          adminService.updatePassword(admin, form.getNewPassword());
          redirectAttributes.addFlashAttribute("success", "Password updated successfully.");
          return "redirect:/admin/changePassword";
      }
      @GetMapping("/tickets/export/excel")
      public void exportToExcel(HttpServletResponse response) throws IOException {
          response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
          String headerValue = "attachment; filename=tickets.xlsx";
          response.setHeader("Content-Disposition", headerValue);

          List<Tickets> tickets = ticketService.getAllTickets();

          XSSFWorkbook workbook = new XSSFWorkbook();
          XSSFSheet sheet = workbook.createSheet("Tickets");

          // Header
          Row headerRow = sheet.createRow(0);
          String[] columns = {"ID", "Title", "Description", "Status", "Created By", "Created At"};
          for (int i = 0; i < columns.length; i++) {
              Cell cell = headerRow.createCell(i);
              cell.setCellValue(columns[i]);
          }

          // Data rows
          int rowCount = 1;
          for (Tickets ticket : tickets) {
              Row row = sheet.createRow(rowCount++);
              row.createCell(0).setCellValue(ticket.getId());
              row.createCell(1).setCellValue(ticket.getTitle());
              row.createCell(2).setCellValue(ticket.getDescription());
              row.createCell(3).setCellValue(ticket.getStatus().toString());
              row.createCell(4).setCellValue(ticket.getUser().getUsername());
              row.createCell(5).setCellValue(ticket.getCreatedAt().toString());
          }

          workbook.write(response.getOutputStream());
          workbook.close();
      }
      @GetMapping("/users/export/excel")
      public void exportUsersToExcel(HttpServletResponse response) throws IOException {
          response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
          response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

          List<User> users = adminService.getAllUsers(); // assumes this returns List<User>

          XSSFWorkbook workbook = new XSSFWorkbook();
          XSSFSheet sheet = workbook.createSheet("Users");

          // Create header
          Row headerRow = sheet.createRow(0);
          String[] columns = {"ID", "Username", "Email", "Role"};
          for (int i = 0; i < columns.length; i++) {
              Cell cell = headerRow.createCell(i);
              cell.setCellValue(columns[i]);
          }

          // Fill data
          int rowNum = 1;
          for (User user : users) {
              Row row = sheet.createRow(rowNum++);
              row.createCell(0).setCellValue(user.getId());
              row.createCell(1).setCellValue(user.getUsername());
              row.createCell(2).setCellValue(user.getEmail());
              row.createCell(3).setCellValue(user.getRole()); // assuming you have a getRole()
          }

          workbook.write(response.getOutputStream());
          workbook.close();
      }
//      @GetMapping("/sla-breaches")
//      public String viewSlaBreaches(Model model) {
//          List<Tickets> tickets = ticketService.getAllTickets();
//
//          List<TicketSlaInfo> slaTickets = tickets.stream()
//              .filter(ticket -> ticket.getPriority() != null && ticket.getCreatedAt() != null)
//              .map(ticket -> {
//                  LocalDateTime slaDue = ticketService.getSlaDue(ticket);
//                  boolean breached = ticketService.isSlaBreached(ticket);
//                  return new TicketSlaInfo(ticket, slaDue, breached);
//              }).collect(Collectors.toList());
//
//          model.addAttribute("slaTickets", slaTickets);
//          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
//	      model.addAttribute("role", "ADMIN");
//	      model.addAttribute("currentPage", "slaBreaches");
//
//          return "slaBreaches";
//      }
      @GetMapping("/sla-breaches")
      public String viewSlaBreaches(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {

          Page<TicketSlaInfo> slaTicketsPage = ticketService.getPaginatedSlaTickets(page, size);

          model.addAttribute("slaTickets", slaTicketsPage);
          model.addAttribute("pageIndex", page);
          model.addAttribute("totalPages", slaTicketsPage.getTotalPages());

          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
          model.addAttribute("role", "ADMIN");
          model.addAttribute("currentPage", "slaBreaches");

          return "slaBreaches";
      }

      @GetMapping("/departments")
      public String viewDepartments(Model model) {
          model.addAttribute("departments", departmentService.getAllDepartments());
          if (!model.containsAttribute("department")) {
              model.addAttribute("department", new Department());
              model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
    	      model.addAttribute("role", "ADMIN");
    	      model.addAttribute("currentPage", "manageDepartments");

          }         
          return "manageDepartments";
      }

      @PostMapping("/departments/add")
      public String addDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttrs) {
          departmentService.saveDepartment(department);
          redirectAttrs.addFlashAttribute("success", "Department added successfully.");
          return "redirect:/admin/departments";
      }
      @GetMapping("/departments/edit/{id}")
      public String editDepartment(@PathVariable Long id, Model model) {
          Department department = departmentService.getById(id);
          model.addAttribute("department", department);
          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	      model.addAttribute("role", "ADMIN");
          model.addAttribute("departments", departmentService.getAllDepartments());
	      model.addAttribute("currentPage", "manageDepartments");

          return "manageDepartments";
      }
      @PostMapping("/departments/update")
      public String updateDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttrs) {
          departmentService.saveDepartment(department); // same as add; saves updated
          redirectAttrs.addFlashAttribute("success", "Department updated successfully.");
          return "redirect:/admin/departments";
      }
      @PostMapping("/departments/delete-ticket/{id}")
      public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttrs) {
          departmentService.deleteById(id);

          redirectAttrs.addFlashAttribute("success", "Department deleted successfully.");
          return "redirect:/admin/departments";
      }
     
      @GetMapping("/departments-dashboard")
      public String departmentsDashboard(Model model) {
    	  model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	      model.addAttribute("role", "ADMIN");
	      model.addAttribute("currentPage", "departmentDashboard");

          return "departmentDashboard";
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
      @PostMapping("/tickets/updateStatus")
      public String updateTicketStatus(@RequestParam Long ticketId,
                                       @RequestParam TicketStatus status,
                                       @RequestParam String remarks,
                                       Principal principal) {
          Tickets ticket = ticketRepository.findById(ticketId)
                  .orElseThrow(() -> new RuntimeException("Ticket not found"));

          ticket.setStatus(status);
          ticketRepository.save(ticket);

          // Log to history with remarks
          ticketService.logTicketHistory(
                  ticket,
                  "Status Updated to " + status,
                  remarks, // This is the admin's entered remark
                  principal.getName()
          );

          return "redirect:/admin/manageTickets";
      }


      @PostMapping("/tickets/updateLifecycle")
      public String updateTicketLifecycle(@RequestParam("ticketId") Long ticketId,
                                          @RequestParam("lifecycleStage") LifecycleStage newStage,
                                          Principal principal) {
          ticketService.updateLifecycleStage(ticketId, newStage, principal.getName());
          return "redirect:/admin/manageTickets";
      }

      @GetMapping("/ticket-lifecycle/{ticketId}")
      public String viewLifecycle(@PathVariable Long ticketId, Model model) {
          Tickets ticket = ticketRepository.findById(ticketId)
              .orElseThrow(() -> new RuntimeException("Ticket not found"));
          List<TicketLifecycleHistory> lifecycle = ticketLifecycleHistoryRepository.findByTicketOrderByUpdatedAtDesc(ticket);

          model.addAttribute("ticket", ticket);
          model.addAttribute("lifecycleList", lifecycle);
          return "fragments/ticketLifecycle :: trackingContent"; // return fragment only
      }
      @PostMapping("/assignTicket")
      public String assignTicketToUser(@RequestParam Long ticketId,
                                       @RequestParam Long userId,
                                       RedirectAttributes redirectAttributes) {
          ticketService.assignTicketToUser(ticketId, userId);
          redirectAttributes.addFlashAttribute("success", "Ticket assigned to user successfully.");
          return "redirect:/admin/manageTickets";
      }
      @GetMapping("/manageCustomers")
      public String viewCustomers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  Model model) {

          Pageable pageable = PageRequest.of(page, size);
          Page<Customer> customerPage = customerServiceImpl.getAllCustomersPaginated(pageable);

          model.addAttribute("customers", customerPage.getContent());
          model.addAttribute("totalPages", customerPage.getTotalPages());
          model.addAttribute("currentPageIndex", page);
          model.addAttribute("size", size); 

          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
          model.addAttribute("role", "ADMIN");
          model.addAttribute("currentPage", "manageCustomers");

          return "manageCustomers"; 
      }


      @GetMapping("/editCustomer/{id}")
      public String editCustomerForm(@PathVariable Long id, Model model) {
          model.addAttribute("customer", customerServiceImpl.getCustomerById(id));
          model.addAttribute("pageTitle", "TICKET MANAGEMENT SYSTEM");
	      model.addAttribute("role", "ADMIN");
          return "editCustomer";
      }

      @PostMapping("/updateCustomer")
      public String updateCustomer(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
    	  customerServiceImpl.updateCustomer(customer);
          redirectAttributes.addFlashAttribute("success", "Customer updated successfully.");
          return "redirect:/admin/manageCustomers";
      }

      @PostMapping("/deleteCustomer/{id}")
      public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    	  customerServiceImpl.deleteCustomer(id);
          redirectAttributes.addFlashAttribute("success", "Customer deleted successfully.");
          return "redirect:/admin/manageCustomers";
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
      @PostMapping("/tickets/fullAssignment")
      public String assignTicketDetails(@RequestParam Long ticketId,
                                        @RequestParam String status,
                                        @RequestParam String lifecycleStage,
                                        @RequestParam Long userId,
                                        @RequestParam Long departmentId,
                                        @RequestParam String description,
                                        Principal principal) {

          ticketService.updateAllAssignmentDetails(ticketId, status, lifecycleStage, userId, departmentId, principal.getName(), description);
          return "redirect:/admin/manageTickets";
      }

}
