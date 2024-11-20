package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServerThread extends Thread {

	private Socket connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String msg;
	int num1, num2;
	String choice;
	
	private static Map<String, Report> reports = new HashMap<>(); // username-password store
    private static Map<String, Employee> accounts = new HashMap<>(); // Report ID-name store
    private static int ReportIdCounter = 1;
    private String curLoginId = null;
	
	public ServerThread(Socket myConnection)
	{
		connection = myConnection;
	}
	
	public void run()
	{
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
            while (true) {
                sendMessage("Welcome! Choose an option: REGISTER, LOGIN, or EXIT");
                String command = (String)in.readObject();
                System.out.println(command);
                System.out.println(command.length());

                if ("REGISTER".equalsIgnoreCase(command)) {
                    register();
                } else if ("LOGIN".equalsIgnoreCase(command)) {
                    if (login()) {
                        manageReports();
                    }
                } else if ("EXIT".equalsIgnoreCase(command)) {
                	sendMessage("Goodbye!");
                	in.readObject();
                    break;
                } else {
                    sendMessage("Invalid command.");
                    in.readObject();
                }
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
                connection.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
		
		
		
	}
	
	
	
	private void menu() {
		sendMessage("----- Accident Report Service -----\n"
				+ "1. Create Report\n"
				+ "2. Retrieve all Reports\n"
				+ "3. Assign Report\n"
				+ "4. View all of your Reports\n"
				+ "5. Update password\n");
	}
	
	private void loginMenu() {
		sendMessage("----- Accident Report Service -----\n"
				+ "1. Register\n"
				+ "2. Login\n");
	}
	
	private void register() throws IOException, ClassNotFoundException {
        sendMessage("Enter email:");
        String email = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();

        if (accounts.containsKey(email)) {
            sendMessage("Account with this email already exists.");
            in.readObject();
        } else {
        	
        	sendMessage("Enter name:");
            String name = (String)in.readObject();
        	
        	StringBuilder text = new StringBuilder("Enter department: \n");
        	for (Department d: Department.values()) {  
        	    text.append(d + "\n"); 
        	}
        	String deptStr;
        	boolean isValid = false;
        	Department dept = Department.Cork;
        	do {
        		sendMessage(text.toString());
            	deptStr = (String)in.readObject();
            	for (Department d: Department.values()) {
            		if (deptStr.equalsIgnoreCase(d.name()) ) { 
            			dept = d;
            			isValid = true;
            		}
            	}
        	} while (!isValid);
        	
        	
        	text = new StringBuilder("Enter role: \n");
        	for (Role r: Role.values()) { 
        	    text.append(r + "\n"); 
        	}
        	String roleStr;
        	Role role = Role.BackEnd;
        	isValid = false;
        	do {
        		sendMessage(text.toString());
            	roleStr = (String)in.readObject();
            	for (Role r: Role.values()) {
            		if (roleStr.equalsIgnoreCase(r.name()) ) { 
            			role = r;
            			isValid = true;
            		}
            	}
        	} while (!isValid);
        	
            accounts.put(email, 
            		new Employee(
            		name,
            		email,
            		password,
            		dept,
            		role
            		));
            
            sendMessage("Registration successful!\n"
            		+ "Registered with " + email);
            in.readObject();
        }
    }

    private boolean login() throws IOException, ClassNotFoundException {
        sendMessage("Enter email:");
        String email = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();
        
        if ((accounts.containsKey(email) 
        		&& password.equals(accounts.get(email).password()))) {
            sendMessage("Login successful!");
            in.readObject();
            curLoginId = accounts.get(email).id();
            return true;
        } else {
            sendMessage("Invalid username or password.");
            in.readObject();
            return false;
        }
    }

    private void manageReports() throws IOException, ClassNotFoundException {
        while (true) {
            sendMessage("Report Management: \n"
            		+ "ADD a report \n"
            		+ "VIEW all reports \n"
            		+ "UPDATE report status \n"
            		+ "SEE my reports \n"
            		+ "LOGOUT \n");
            String command = (String)in.readObject();

            if ("ADD".equalsIgnoreCase(command)) {
                addReport();
            } else if ("VIEW".equalsIgnoreCase(command)) {
                viewReports();
            } else if ("UPDATE".equalsIgnoreCase(command)) {
                updateReport();
            } else if ("SEE".equalsIgnoreCase(command)) {
                seeMyReports();
            } else if ("LOGOUT".equalsIgnoreCase(command)) {
            	curLoginId = null;
                sendMessage("Logged out.");
                in.readObject();
                break;
            } else {
                sendMessage("Invalid command.");
                in.readObject();
            }
        }
    }

    private void addReport() throws IOException, ClassNotFoundException {
    	
    	// ReportType entry
    	StringBuilder text = new StringBuilder("Enter Report Type: \n");
    	for (ReportType rt: ReportType.values()) {  
    	    text.append(rt + "\n"); 
    	}
    	String repTypeStr;
    	boolean isValid = false;
    	ReportType repType = ReportType.ACCIDENT;
    	do {
    		sendMessage(text.toString());
        	repTypeStr = (String)in.readObject();
        	for (ReportType d: ReportType.values()) {
        		if (repTypeStr.equalsIgnoreCase(d.name()) ) { 
        			repType = d;
        			isValid = true;
        		}
        	}
    	} while (!isValid);
    	
    	// ReportStatus entry
    	StringBuilder statusText = new StringBuilder("Enter Report Status: \n");
    	for (ReportStatus rs : ReportStatus.values()) {  
    	    statusText.append(rs + "\n"); 
    	}
    	String repStatusStr;
    	isValid = false;
    	ReportStatus repStatus = ReportStatus.ASSIGNED;
    	do {
    		sendMessage(statusText.toString());
        	repStatusStr = (String)in.readObject();
        	for (ReportStatus rs: ReportStatus.values()) {
        		if (repStatusStr.equalsIgnoreCase(rs.name()) ) { 
        			repStatus = rs;
        			isValid = true;
        		}
        	}
    	} while (!isValid);
    	
    	// WILL CHANGE
    	// LocalDate entry - LocalDate.now() for the moment 
        LocalDate date = LocalDate.now();
        
        // empId entry
        StringBuilder empText = new StringBuilder("Enter Assigned Employee (Enter NULL if unassigned): \n");
        Set<String> ids = new HashSet<String>();
        accounts.forEach((email, account) -> {
        	empText.append(account.id() + " - " + account.name() + "\n");
        	ids.add(account.id().toLowerCase());
        });
    	String assignedId;
    	isValid = false;
    	do {
    		sendMessage(empText.toString());
        	assignedId = (String)in.readObject();
    	} while (!assignedId.equalsIgnoreCase("NULL") && !ids.contains(assignedId.toLowerCase()));
        
        Report report = new Report(repType, date, curLoginId, assignedId, repStatus);
        
        reports.put(report.id().toLowerCase(), report);
        sendMessage("Report added with ID: " + report.id());
        in.readObject();
    }

    private void viewReports() throws ClassNotFoundException, IOException {
        if (reports.isEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder reportList = new StringBuilder("----- Report List -----\n");
            reports.forEach((id, report) -> {
            	reportList.append("Report " + report.id() + ": \n" + 
            					report.toString() + "\n");
            });
            sendMessage(reportList.toString());
            in.readObject();
        }
    }

    // reassign / change status of report with given id
    private void updateReport() throws IOException, ClassNotFoundException {
    	System.out.println(reports.toString());
        sendMessage("Enter Report ID to update:");
        String id = (String)in.readObject();
        if (reports.containsKey(id.toLowerCase())) {
        	StringBuilder assignedText = new StringBuilder("Enter new assigned employee ID (Enter NULL if unassigned): \n");
        	Set<String> empIds = new HashSet<>();
        	accounts.forEach((email, employee) -> {
            	assignedText.append(employee.id() + " - " + employee.name() + "\n");
            	empIds.add(employee.id().toLowerCase());
            });
        	String newEmpId;
        	do {
        		sendMessage(assignedText.toString());
                newEmpId = (String)in.readObject();
        	} while (!newEmpId.equalsIgnoreCase("NULL") && !empIds.contains(newEmpId.toLowerCase()));
        	            
            reports.get(id).setAssignedId(newEmpId);
            
            // update status
         // ReportType entry
        	StringBuilder statusText = new StringBuilder("Enter Status: \n");
        	for (ReportStatus rs: ReportStatus.values()) {  
        	    statusText.append(rs + "\n"); 
        	}
        	String statusStr;
        	boolean isValid = false;
        	ReportStatus status = ReportStatus.ASSIGNED;
        	do {
        		sendMessage(statusText.toString());
            	statusStr = (String)in.readObject();
            	for (ReportStatus rs: ReportStatus.values()) {
            		if (statusStr.equalsIgnoreCase(rs.name()) ) { 
            			status = rs;
            			isValid = true;
            		}
            	}
        	} while (!isValid);
        	
        	reports.get(id.toLowerCase()).setStatus(status);
            
            sendMessage("Report updated.");
            in.readObject();
        } else {
            sendMessage("Report not found.");
            in.readObject();
        }
    }

    private void seeMyReports() throws IOException, NumberFormatException, ClassNotFoundException {
    	if (reports.isEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder reportList = new StringBuilder("----- Your Report List -----\n\n");
            reports.forEach((id, report) -> {
            	if (report.assignedId().equalsIgnoreCase(curLoginId)) {
            		reportList.append("Report " + report.id() + ": \n" + 
        					report.toString() + "\n");
            	}
            	
            });
            sendMessage(reportList.toString());
            in.readObject();
        }
    }
    
    /*
    private void deleteReport() throws IOException, NumberFormatException, ClassNotFoundException {
        sendMessage("Enter Report ID to delete:");
        int id = Integer.parseInt((String)in.readObject());
        if (reports.remove(id) != null) {
            sendMessage("Report deleted.");
            in.readObject();
        } else {
            sendMessage("Report not found.");
            in.readObject();
        }
    }
    */
    
    /*
    private Object userEnumType(enum E) {
    	StringBuilder text = new StringBuilder("Enter x: \n");
    	for (E value: E.values()) {  
    	    text.append(value + "\n"); 
    	}
    	
    	String valueStr;
    	boolean isValid = false;
    	E eType;
    	do {
    		sendMessage(text.toString());
        	valueStr = (String)in.readObject();
        	for (E value: E.values()) {
        		if (valueStr.equalsIgnoreCase(value.name()) ) { 
        			eType = value;
        			isValid = true;
        		}
        	}
    	} while (!isValid);
    }
    */
	
	private void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
}
