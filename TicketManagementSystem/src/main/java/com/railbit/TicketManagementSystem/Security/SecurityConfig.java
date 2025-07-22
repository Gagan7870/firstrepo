package com.railbit.TicketManagementSystem.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.railbit.TicketManagementSystem.Service.CustomAdminDetailService;

    @Configuration
	@EnableWebSecurity
	public class SecurityConfig {
    	
    	
    	 @Bean
    	    public UserDetailsService userDetailsService() {
    	        return new CustomAdminDetailService() ;
    	    }
    	    @Bean
    	    public DaoAuthenticationProvider authenticationProvider() {
    	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	        provider.setUserDetailsService(userDetailsService());
    	        provider.setPasswordEncoder(passwordEncoder());
    	        return provider;
    	    }
	    @Bean
	    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
	        return new RoleBasedSuccessHandler(); 
	    }

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf().disable()
	            .authorizeHttpRequests(auth -> auth
	            	            .requestMatchers(
	            	                "/home", "/css/**", "/js/**", "/images/**", "/fragments/**",
	            	                "/customer/register", "/customer/saveCustomer", // ✅ fixed path
	            	                "/register", "/saveUser",
	            	                "/webjars/**", "/chat-websocket/**",
	            	                "/forget-password",
	            	                "/send-reset-link",
	            	                "/reset-password",
	            	                "/reset-password/**"
	            	            ).permitAll()

	            	            .requestMatchers("/admin/**").hasRole("ADMIN")
	            	            .requestMatchers("/user/**").hasRole("USER")
	            	            .requestMatchers("/customer/**").hasRole("CUSTOMER") // only after public paths
	            	            .anyRequest().authenticated()
	            	        )
	        .formLogin(form -> form
	                .loginPage("/login")
	                .loginProcessingUrl("/perform_login")
	                .usernameParameter("username")
	                .passwordParameter("password")
	                .successHandler(myAuthenticationSuccessHandler())
	                .failureUrl("/login_Failed")
	                .permitAll()
	                
	            )	
	            .logout(logout -> logout
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/login?logout=true")
	                .permitAll()	
	            );
	        return http.build();
	    }
	    

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
//	    @Bean
//	    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//	        provider.setUserDetailsService(userDetailsService());
//	        provider.setPasswordEncoder(passwordEncoder());
//
//	        AuthenticationManagerBuilder authenticationManagerBuilder =
//	                http.getSharedObject(AuthenticationManagerBuilder.class);
//	        authenticationManagerBuilder.authenticationProvider(provider);
//	        
//	        return authenticationManagerBuilder.build();
//	    }
	    @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
	            throws Exception {
	        return config.getAuthenticationManager();
	    }
	}


