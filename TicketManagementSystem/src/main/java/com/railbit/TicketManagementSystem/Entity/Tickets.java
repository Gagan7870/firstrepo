package com.railbit.TicketManagementSystem.Entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Tickets{

    @Id 
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private TicketStatus status = TicketStatus.OPEN;

    @ManyToOne
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	@ManyToOne
    @JoinColumn(name = "created_by_customer_id")
    private Customer createdByCustomer; 

    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="customer_id")
    private Customer customer;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date slaDueDate;

    private boolean slaBreached;
    
    @Enumerated(EnumType.STRING)
    private LifecycleStage lifecycleStage = LifecycleStage.NEW;
    @Lob
    @Column(name = "attachment", columnDefinition = "LONGBLOB")
    private byte[] attachment;

    @Column(name = "attachment_filename")
    private String attachmentFilename;
    
    
    public byte[] getAttachment() {
		return attachment;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public String getAttachmentFilename() {
		return attachmentFilename;
	}

	public void setAttachmentFilename(String attachmentFilename) {
		this.attachmentFilename = attachmentFilename;
	}

	public List<TicketLifecycleHistory> getLifecycleHistories() {
		return lifecycleHistories;
	}

	public void setLifecycleHistories(List<TicketLifecycleHistory> lifecycleHistories) {
		this.lifecycleHistories = lifecycleHistories;
	}

	public LifecycleStage getLifecycleStage() {
		return lifecycleStage;
	}

	public void setLifecycleStage(LifecycleStage lifecycleStage) {
		this.lifecycleStage = lifecycleStage;
	}
	
	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketHistory> historyList;

    public List<TicketHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<TicketHistory> historyList) {
        this.historyList = historyList;
    }
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketLifecycleHistory> lifecycleHistories;


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public Customer getCreatedByCustomer() {
		return createdByCustomer;
	}

	public void setCreatedByCustomer(Customer createdByCustomer) {
		this.createdByCustomer = createdByCustomer;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Date getSlaDueDate() {
		return slaDueDate;
	}

	public void setSlaDueDate(Date slaDueDate) {
		this.slaDueDate = slaDueDate;
	}

	public boolean isSlaBreached() {
		return slaBreached;
	}

	public void setSlaBreached(boolean slaBreached) {
		this.slaBreached = slaBreached;
	}

	@Override
	public String toString() {
		return "Tickets [id=" + id + ", title=" + title + ", description=" + description + ", priority=" + priority
				+ ", status=" + status + ", createdBy=" + createdBy + ", createdAt=" + createdAt + ", user=" + user
				+ "]";
	}






    
    
}
