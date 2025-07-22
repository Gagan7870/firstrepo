package com.railbit.TicketManagementSystem.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SLAConfiguration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
	@Enumerated(EnumType.STRING)
	private Priority priority;
	
	private int resolutionHours;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public int getResolutionHours() {
		return resolutionHours;
	}

	public void setResolutionHours(int resolutionHours) {
		this.resolutionHours = resolutionHours;
	}

	@Override
	public String toString() {
		return "SLAConfiguration [id=" + id + ", priority=" + priority + ", resolutionHours=" + resolutionHours + "]";
	}

	public SLAConfiguration( Priority priority, int resolutionHours) {
		super();
		
		this.priority = priority;
		this.resolutionHours = resolutionHours;
	}

	public SLAConfiguration() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
