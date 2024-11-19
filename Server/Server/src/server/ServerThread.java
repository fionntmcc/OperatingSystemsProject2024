package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
        	
            accounts.put("123",
            		new Employee(
                    		name,
                    		"hello",
                    		email,
                    		password,
                    		dept,
                    		role
                    		));
            sendMessage("Registration successful!");
            in.readObject();
        }
    }

    private boolean login() throws IOException, ClassNotFoundException {
        sendMessage("Enter email:");
        String email = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();
        
        System.out.println(accounts.containsKey(email));
        System.out.println(password.equals(accounts.get(email).password()));

        if (accounts.containsKey(email) 
        		&& password.equals(accounts.get(email).password())) {
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
        sendMessage("Enter Report name:");
        String name = (String)in.readObject();
        
        reports.put("EMP" + ReportIdCounter, new Report(name, null, null, name, null));
        sendMessage("Report added with ID: " + ReportIdCounter);
        in.readObject();
        ReportIdCounter++;
    }

    private void viewReports() throws ClassNotFoundException, IOException {
        if (reports.isEmpty()) {
            sendMessage("No Reports found.");
            in.readObject();
        } else {
        	StringBuilder ReportList = new StringBuilder("Report List:\n");
            reports.forEach((id, name) -> {
            	ReportList.append("ID: " + id + ", Name: " + name + "\n");
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
            reports.put(id, new Report(newName, null, null, newName, null));
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
