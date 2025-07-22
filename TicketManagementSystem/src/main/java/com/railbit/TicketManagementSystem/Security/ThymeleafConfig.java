package com.railbit.TicketManagementSystem.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ThymeleafConfig {
	
	    @Bean
	    @RequestScope
	    public HttpServletRequest request(HttpServletRequest request) {
	        return request;
	    }
}
