package com.railbit.TicketManagementSystem.Entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Department {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 
	private String name;
	
	private String description;
	
	@OneToMany(mappedBy = "department",fetch=FetchType.LAZY)
	private List<Tickets> tickets;

	public Department(Long id, String name, String description, List<Tickets> tickets) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.tickets = tickets;
	}

	public Department() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Tickets> getTickets() {
		return tickets;
	}

	public void setTickets(List<Tickets> tickets) {
		this.tickets = tickets;
	}

	@Override 
	public String toString() {
		return "Department [id=" + id + ", name=" + name + ", description=" + description + ", tickets=" + tickets
				+ "]";
	}
}
