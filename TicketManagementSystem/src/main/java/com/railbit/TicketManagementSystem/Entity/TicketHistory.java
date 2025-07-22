package com.railbit.TicketManagementSystem.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TicketHistory {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "ticket_id", nullable = false)

	    private Tickets ticket;

	    private String action; 

	    private String description; 

	    private String updatedBy; 

	    private LocalDateTime updatedAt = LocalDateTime.now();

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Tickets getTicket() {
			return ticket;
		}

		public void setTicket(Tickets ticket) {
			this.ticket = ticket;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUpdatedBy() {
			return updatedBy;
		}

		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		public TicketHistory(Long id, Tickets ticket, String action, String description, String updatedBy,
				LocalDateTime updatedAt) {
			super();
			this.id = id;
			this.ticket = ticket;
			this.action = action;
			this.description = description;
			this.updatedBy = updatedBy;
			this.updatedAt = updatedAt;
		}

		public TicketHistory() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public String toString() {
			return "TicketHistory [id=" + id + ", ticket=" + ticket + ", action=" + action + ", description="
					+ description + ", updatedBy=" + updatedBy + ", updatedAt=" + updatedAt + "]";
		}
	    
	    
}
