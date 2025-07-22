package com.railbit.TicketManagementSystem.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Customer {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@NotBlank(message="Username is mandatory")
	private String username;
	@NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
	private String phone;
	@Column(unique = true)
    private String email;
	private String address;
	private String password;
	private String role;
    private String resetToken;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	@Override
	public String toString() {
		return "Customer [id=" + id + ", username=" + username + ", phone=" + phone + ", email=" + email + ", address="
				+ address + ", password=" + password + ", role=" + role + "]";
	}
	public Customer(Long id, @NotBlank(message = "Username is mandatory") String username,
			@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number") String phone,
			String email, String address, String password, String role) {
		super();
		this.id = id;
		this.username = username;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.password = password;
		this.role = role;
	}
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	
}
