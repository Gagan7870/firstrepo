package com.railbit.TicketManagementSystem.Entity;

import java.time.LocalDateTime;

public class TicketSlaInfo {
     
	    private Tickets ticket;
	    private LocalDateTime slaDue;
	    private boolean breached;
		public Tickets getTicket() {
			return ticket;
		}
		public void setTicket(Tickets ticket) {
			this.ticket = ticket;
		}
		public LocalDateTime getSlaDue() {
			return slaDue;
		}
		public void setSlaDue(LocalDateTime slaDue) {
			this.slaDue = slaDue;
		}
		public boolean isBreached() {
			return breached;
		}
		public void setBreached(boolean breached) {
			this.breached = breached;
		}
		public TicketSlaInfo(Tickets ticket, LocalDateTime slaDue, boolean breached) {
			super();
			this.ticket = ticket;
			this.slaDue = slaDue;
			this.breached = breached;
		}
		public TicketSlaInfo() {
			super();
			// TODO Auto-generated constructor stub
		}
		@Override
		public String toString() {
			return "TicketSlaInfo [ticket=" + ticket + ", slaDue=" + slaDue + ", breached=" + breached + "]";
		}
         
	    
	    
	}


