package com.railbit.TicketManagementSystem.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.railbit.TicketManagementSystem.Entity.ChatMessage;
import com.railbit.TicketManagementSystem.Service.UserService;


@Controller
public class ChatConroller {
	
	@Autowired
    private SimpMessagingTemplate messagingTemplate;
	
	
    @MessageMapping("/chat/{target}")
    public void sendMessage(@DestinationVariable String target, ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/chat-" + target, message);
    }
//	   @MessageMapping("/chat")
//	    @SendTo("/topic/messages")
//	    public ChatMessage send(ChatMessage message) throws Exception {
//	        return message;
//	    }

	    @GetMapping("/admin/chat")
	    public String chatPage(Model model, Principal principal) {
	        String username = (principal != null) ? principal.getName() : "Guest";

	    	model.addAttribute("role","ADMIN");
	    	model.addAttribute("currentPage","chat");
	        model.addAttribute("username",username);
	        return "chat";
	    }
	    @GetMapping("/user/chat")
	    public String chatPage1(Model model, Principal principal) {
	       String username = (principal != null) ? principal.getName() : "Guest";

	    	model.addAttribute("role","USER");
	    	model.addAttribute("currentPage","chat");
	        model.addAttribute("username", username);
	        return "chat";
	    }
	    @GetMapping("/customer/chat")
	    public String chatPage2(Model model, Principal principal) {
	        String username = (principal != null) ? principal.getName() : "Guest";

	    	model.addAttribute("role","CUSTOMER");
	    	model.addAttribute("currentPage","chat");
	        model.addAttribute("username", username);
	        return "chat";
	    }
}
