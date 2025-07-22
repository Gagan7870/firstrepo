package com.railbit.TicketManagementSystem.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TicketLifecycleHistory {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "ticket_id", nullable = false)

	    private Tickets ticket;

	    @Enumerated(EnumType.STRING)
	    private LifecycleStage fromStage;

	    @Enumerated(EnumType.STRING)
	    private LifecycleStage toStage;

	    private LocalDateTime updatedAt;

	    private String updatedBy;

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

		public LifecycleStage getFromStage() {
			return fromStage;
		}

		public void setFromStage(LifecycleStage fromStage) {
			this.fromStage = fromStage;
		}

		public LifecycleStage getToStage() {
			return toStage;
		}

		public void setToStage(LifecycleStage toStage) {
			this.toStage = toStage;
		}

		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}

		public String getUpdatedBy() {
			return updatedBy;
		}

		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}

		public TicketLifecycleHistory() {
			super();
			// TODO Auto-generated constructor stub
		}

		public TicketLifecycleHistory(Long id, Tickets ticket, LifecycleStage fromStage, LifecycleStage toStage,
				LocalDateTime updatedAt, String updatedBy) {
			super();
			this.id = id;
			this.ticket = ticket;
			this.fromStage = fromStage;
			this.toStage = toStage;
			this.updatedAt = updatedAt;
			this.updatedBy = updatedBy;
		}
	    
}
