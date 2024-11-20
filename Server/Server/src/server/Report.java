package server;

import java.time.LocalDate;

public class Report {
	
	private String id;
	private ReportType type;
	private LocalDate date;
	private String creatorId, assignedId;
	private ReportStatus status;
	
	public Report( 
			String id,
			ReportType type, 
			LocalDate date, 
			String creatorId,
			String assignedId,
			ReportStatus status) {
		this.id = id;
		this.type = type;
		this.date = date;
		this.creatorId = creatorId;
		this.assignedId = assignedId;
		this.status = status;
	}
	
	public void setAssignedId(String newId) {
		this.assignedId = newId;
	}
	
	public void setStatus(ReportStatus status) {
		this.status = status;
	}
	
	public ReportType type() {
		return type;
	}

	public LocalDate date() {
		return date;
	}

	public String creatorId() {
		return creatorId;
	}
	
	public String assignedId() {
		return assignedId;
	}

	public ReportStatus status() {
		return status;
	}

	public String id() {
		return id;
	}

	@Override
	public String toString() {
		return ("Report id: " + id() + "\n" +
				"Type: " + type() + "\n" +
				"Date: " + date().toString() + "\n" +
				"Created by: " + creatorId() + "\n" +
				"Assigned Employee: " + assignedId() + "\n" +
				"Status: " + status() + "\n"
				);
	}
	

}
