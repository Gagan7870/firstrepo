package com.railbit.TicketManagementSystem.Entity;

public class ChatMessage {
	
	private String from;
	private String content;
	public ChatMessage(String from, String content) {
		super();
		this.from = from;
		this.content = content;
	}
	public ChatMessage() {
		super();
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	

}
