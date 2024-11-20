package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Provider{
	
	private static Map<String, Employee> employeeMap = new ConcurrentHashMap<>();
	private static Map<String, Report> reportMap = new ConcurrentHashMap<>();
	
	private final static String ACCOUNT_FILE_NAME = "accounts.txt";
	private final static String REPORT_FILE_NAME = "reports.txt";
	
	
	
	public static void main(String args[])
	{
		createEmployeeMap();
		createReportMap();
		
		//System.out.println(employeeMap.toString());
		//System.out.println(reportMap.toString());
		
		ServerSocket providerSocket;
		Socket connection = null;
		ServerThread handler;
		
		try
		{
			providerSocket = new ServerSocket(2004, 10);
		
			while(true)
			{
			
				System.out.println("Waiting for connection");
				connection = providerSocket.accept();
				handler = new ServerThread(connection);
				handler.start();
				System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			}
		}
		
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	private static void createEmployeeMap() {
		File file = new File(ACCOUNT_FILE_NAME);
		try {
			Scanner fileReader = new Scanner(file);
			while(fileReader.hasNextLine()) {
				String text = fileReader.nextLine();
				String[] params = text.split(" ");
				if (params.length != 6) {
					System.out.println("Error reading " + ACCOUNT_FILE_NAME);
				} else {
					Department dept = Department.Cork;
		        	for (Department d: Department.values()) {
		        		if (params[4].equalsIgnoreCase(d.name()) ) { 
		        			dept = d;
		        		}
		        	}
		        	
		        	Role role = Role.BackEnd;
		        	for (Role r: Role.values()) {
		        		if (params[5].equalsIgnoreCase(r.name()) ) { 
		        			role = r;
		        		}
		        	}
					
					employeeMap.put(params[0], 
							new Employee(params[0], 
									params[1], 
									params[2], 
									params[3], 
									dept, 
									role));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createReportMap() {
		File file = new File(REPORT_FILE_NAME);
		try {
			Scanner fileReader = new Scanner(file);
			while(fileReader.hasNextLine()) {
				String text = fileReader.nextLine();
				String[] params = text.split(" ");
				if (params.length != 6) {
				} else {
					
					ReportType type = ReportType.ACCIDENT;
		        	for (ReportType rt: ReportType.values()) {
		        		if (params[1].equalsIgnoreCase(rt.name()) ) { 
		        			type = rt;
		        		}
		        	}
		        	
		        	ReportStatus status = ReportStatus.ASSIGNED;
		        	for (ReportStatus s: ReportStatus.values()) {
		        		if (params[5].equalsIgnoreCase(s.name()) ) { 
		        			status = s;
		        		}
		        	}
					
					reportMap.put(params[0], 
							new Report(params[0], 
									type, 
									LocalDate.parse(params[2]), 
									params[3], 
									params[4], 
									status));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeEmployees() {
		File file = new File(ACCOUNT_FILE_NAME);
		try {
			FileWriter writer = new FileWriter(file);
			StringBuilder text = new StringBuilder("");
			employeeMap.forEach((email, employee) -> {
				text.append(employee.id() + " " +
						employee.name() + " " + 
						employee.email() + " " + 
						employee.password() + " " + 
						employee.dept().name() + " " + 
						employee.role().name() + "\n");
			});
			System.out.println(text.toString());
			writer.write(text.toString());
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeReports() {
		File file = new File(REPORT_FILE_NAME);
		try {
			FileWriter writer = new FileWriter(file);
			StringBuilder text = new StringBuilder("");
			reportMap.forEach((id, report) -> {
				text.append(report.id() + " " +
						report.type().name() + " " +
						report.date() + " " +
						report.creatorId() + " " +
						report.assignedId() + " " +
						report.status().name() + "\n");
			});
			System.out.println(text.toString());
			writer.write(text.toString());
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getNumEmployees() {
		return employeeMap.size();
	}
	
	public static int getNumReports() {
		return reportMap.size();
	}
	
	public static void putEmployee(Employee e) {
		employeeMap.put(e.email(), e);
	}
	
	public static void putReport(Report r) {
		reportMap.put(r.id(), r);
	}
	
	public static Employee getEmployee(String key) {
		return employeeMap.get(key);
	}
	
	public static Report getReport(String key) {
		return reportMap.get(key);
	}
	
	
	public static boolean containsEmployeeKey(String key) {
		return employeeMap.containsKey(key);
	}
	
	public static boolean containsReportKey(String key) {
		return reportMap.containsKey(key);
	}
	
	public static HashMap<String, Employee> employeeMap() {
		return new HashMap<String, Employee>(employeeMap);
	}
	
	public static HashMap<String, Report> reportMap() {
		return new HashMap<String, Report>(reportMap);
	}
	
	public static boolean isEmployeesEmpty() {
		return employeeMap.isEmpty();
	}
	
	public static boolean isReportsEmpty() {
		return reportMap.isEmpty();
	}
	
	public static void writeFiles() {
		writeEmployees();
		writeReports();
	}
	
}
