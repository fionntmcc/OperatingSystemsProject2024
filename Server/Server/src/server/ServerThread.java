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
	
	
	/*
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
    */
	
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
        
        System.out.println(accounts.containsKey(email));
        //System.out.println(password.equals(accounts.get(email).password()));
        System.out.println("Password " + accounts.get(email).password());
        System.out.println("Account" + accounts.get(email).toString());
        if ((accounts.containsKey(email) 
        		&& password.equals(accounts.get(email).password()))) {
            sendMessage("Login successful!");
            in.readObject();
            return true;
        } else {
            sendMessage("Invalid username or password.");
            in.readObject();
            return false;
        }
    }

    private void manageReports() throws IOException, ClassNotFoundException {
        while (true) {
            sendMessage("Report Management: ADD, VIEW, UPDATE, DELETE, or LOGOUT");
            String command = (String)in.readObject();

            if ("ADD".equalsIgnoreCase(command)) {
                addReport();
            } else if ("VIEW".equalsIgnoreCase(command)) {
                viewReports();
            } else if ("UPDATE".equalsIgnoreCase(command)) {
                updateReport();
            } else if ("DELETE".equalsIgnoreCase(command)) {
                deleteReport();
            } else if ("LOGOUT".equalsIgnoreCase(command)) {
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
    	text = new StringBuilder("Enter Report Status: \n");
    	for (ReportStatus rs : ReportStatus.values()) {  
    	    text.append(rs + "\n"); 
    	}
    	String repStatusStr;
    	isValid = false;
    	ReportStatus repStatus = ReportStatus.ASSIGNED;
    	do {
    		sendMessage(text.toString());
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
        StringBuilder empText = new StringBuilder("Enter Assigned Employee: \n");
        Set<String> ids = new HashSet<String>();
        accounts.forEach((email, account) -> {
        	empText.append(account.id() + " - " + account.name() + "\n");
        	ids.add(account.id().toLowerCase());
        });
    	String empId;
    	isValid = false;
    	do {
    		sendMessage(empText.toString());
        	empId = (String)in.readObject();
    	} while (!ids.contains(empId.toLowerCase()));
        
        Report report = new Report(repType, date, empId, repStatus);
        
        reports.put(report.id(), report);
        sendMessage("Report added with ID: " + report.id());
        in.readObject();
    }

    private void viewReports() throws ClassNotFoundException, IOException {
        if (reports.isEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder ReportList = new StringBuilder("Report List:\n");
            reports.forEach((id, report) -> {
            	ReportList.append("Report " + report.id() + ": \n" + 
            					report.toString() + "\n");
            });
            sendMessage(ReportList.toString());
            in.readObject();
        }
    }

    private void updateReport() throws IOException, NumberFormatException, ClassNotFoundException {
        sendMessage("Enter Report ID to update:");
        String id = (String)in.readObject();
        if (reports.containsKey(id)) {
            sendMessage("Enter new name:");
            String newName = (String)in.readObject();
            reports.put(id, new Report(null, null, newName, null));
            sendMessage("Report updated.");
            in.readObject();
        } else {
            sendMessage("Report not found.");
            in.readObject();
        }
    }

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
