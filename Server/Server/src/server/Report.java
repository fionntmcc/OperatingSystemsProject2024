package server;

import java.time.LocalDate;

public record Report(String id, 
		ReportType type, 
		LocalDate date, 
		String empId, 
		ReportStatus status) {

}
