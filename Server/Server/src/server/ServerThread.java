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
import java.util.regex.Pattern;

public class ServerThread extends Thread {

	private Socket connection;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	//private static Map<String, Report> reports = new HashMap<>(); // username-password store
    //private static Map<String, Employee> accounts = new HashMap<>(); // Report ID-name store
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
	
	
	private void register() throws IOException, ClassNotFoundException {
		String email;
		do {
			sendMessage("Enter email:");
	        email = (String)in.readObject();
		} while	(!isValidEmail(email));
        
		String password;
        do {
        	sendMessage("Enter password: \n"
        			+ "Password must contain: \n"
        			+ "-> at least 1 uppercase letter \n"
        			+ "-> at least 1 number \n"
        			+ "-> at least 1 symbol \n"
        			+ "-> at least 8 characters");
            password = (String)in.readObject();
        } while (!isValidPassword(password));

        if (Provider.containsEmployeeKey(email)) {
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
        	
            Provider.putEmployee(
            		new Employee(
            		("emp" + Provider.getNumEmployees()),
            		name,
            		email,
            		password,
            		dept,
            		role
            		));
            
            sendMessage("Registration successful! \n"
            		+ "Registered with " + email);
            in.readObject();
        }
    }

    private boolean login() throws IOException, ClassNotFoundException {
        sendMessage("Enter email:");
        String email = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();
        
        if ((Provider.containsEmployeeKey(email) 
        		&& password.equals(Provider.getEmployee(email).password()))) {
            sendMessage("Login successful!");
            in.readObject();
            curLoginId = Provider.getEmployee(email).id();
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
            	Provider.writeFiles();
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
        Provider.employeeMap().forEach((email, account) -> {
        	empText.append(account.id() + " - " + account.name() + "\n");
        	ids.add(account.id().toLowerCase());
        });
    	String assignedId;
    	isValid = false;
    	do {
    		sendMessage(empText.toString());
        	assignedId = (String)in.readObject();
    	} while (!assignedId.equalsIgnoreCase("NULL") && !ids.contains(assignedId.toLowerCase()));
        
        Provider.putReport(new Report(("r" + Provider.getNumReports()), 
        		repType, 
        		date, 
        		curLoginId, 
        		assignedId, 
        		repStatus));
        sendMessage("Report added");
        in.readObject();
    }

    private void viewReports() throws ClassNotFoundException, IOException {
        if (Provider.isReportsEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder reportList = new StringBuilder("----- Report List -----\n");
            Provider.reportMap().forEach((id, report) -> {
            	reportList.append("Report " + report.id() + ": \n" + 
            					report.toString() + "\n");
            });
            sendMessage(reportList.toString());
            in.readObject();
        }
    }

    // reassign / change status of report with given id
    private void updateReport() throws IOException, ClassNotFoundException {
        sendMessage("Enter Report ID to update:");
        String id = (String)in.readObject();
        if (Provider.containsReportKey(id.toLowerCase())) {
        	StringBuilder assignedText = new StringBuilder("Enter new assigned employee ID (Enter NULL if unassigned): \n");
        	Set<String> empIds = new HashSet<>();
        	Provider.employeeMap().forEach((email, employee) -> {
            	assignedText.append(employee.id() + " - " + employee.name() + "\n");
            	empIds.add(employee.id().toLowerCase());
            });
        	String newEmpId;
        	do {
        		sendMessage(assignedText.toString());
                newEmpId = (String)in.readObject();
        	} while (!newEmpId.equalsIgnoreCase("NULL") && !empIds.contains(newEmpId.toLowerCase()));
        	
        	            
            Provider.getReport(id.toLowerCase()).setAssignedId(newEmpId);
            
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
        	
        	Provider.getReport(id.toLowerCase()).setStatus(status);
            
            sendMessage("Report updated.");
            in.readObject();
        } else {
            sendMessage("Report not found.");
            in.readObject();
        }
    }

    private void seeMyReports() throws IOException, NumberFormatException, ClassNotFoundException {
    	if (Provider.isReportsEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder reportList = new StringBuilder("----- Your Report List -----\n\n");
            Provider.reportMap().forEach((id, report) -> {
            	if (report.assignedId().equalsIgnoreCase(curLoginId)) {
            		reportList.append("Report " + report.id() + ": \n" + 
        					report.toString() + "\n");
            	}
            	
            });
            sendMessage(reportList.toString());
            in.readObject();
        }
    }
    
    private static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        // Define the regex pattern for a valid email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        // Use the Pattern class to match the regex against the input email
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false; // Minimum length of 8 characters
        }

        boolean hasUpperCase = false;
        boolean hasNumber = false;
        boolean hasSymbol = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSymbol = true;
            }

            // If all criteria are met, no need to continue checking
            if (hasUpperCase && hasNumber && hasSymbol) {
                return true;
            }
        }

        // Return false if any of the criteria is not met
        return hasUpperCase && hasNumber && hasSymbol;
    }
	
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
