package server;

import java.time.LocalDate;

public class Report {
	
	private ReportType type;
	private LocalDate date;
	private String empId;
	private ReportStatus status;
	
	
	private static int idCounter = 0;
	private int idNum;
	
	public Report( 
			ReportType type, 
			LocalDate date, 
			String empId, 
			ReportStatus status) {
		this.type = type;
		this.date = date;
		this.empId = empId;
		this.status = status;
		
		this.idNum = idCounter++;
	}
	
	public ReportType type() {
		return type;
	}



	public LocalDate date() {
		return date;
	}



	public String empId() {
		return empId;
	}



	public ReportStatus status() {
		return status;
	}



	public String id() {
		return ("R" + idNum);
	}

	@Override
	public String toString() {
		return ("Report id: " + id() + "\n" +
				"Type: " + type() + "\n" +
				"Date: " + date().toString() + "\n" +
				"Assigned Employee: " + empId() + "\n" +
				"Status: " + status() + "\n"
				);
	}
	

}
