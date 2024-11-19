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
	
	private static Map<String, String> accounts = new HashMap<>(); // username-password store
    private static Map<Integer, String> employees = new HashMap<>(); // employee ID-name store
    private static int employeeIdCounter = 1;
	
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

                if ("1".equalsIgnoreCase(command)) {
                    register();
                } else if ("2".equalsIgnoreCase(command)) {
                    if (login()) {
                        manageEmployees();
                    }
                } else if ("3".equalsIgnoreCase(command)) {
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
        sendMessage("Enter username:");
        String username = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();

        if (accounts.containsKey(username)) {
            sendMessage("Username already exists.");
            in.readObject();
        } else {
            accounts.put(username, password);
            sendMessage("Registration successful!");
            in.readObject();
        }
    }

    private boolean login() throws IOException, ClassNotFoundException {
        sendMessage("Enter username:");
        String username = (String)in.readObject();
        sendMessage("Enter password:");
        String password = (String)in.readObject();

        if (password.equals(accounts.get(username))) {
            sendMessage("Login successful!");
            in.readObject();
            return true;
        } else {
            sendMessage("Invalid username or password.");
            in.readObject();
            return false;
        }
    }

    private void manageEmployees() throws IOException, ClassNotFoundException {
        while (true) {
            sendMessage("Employee Management: ADD, VIEW, UPDATE, DELETE, or LOGOUT");
            String command = (String)in.readObject();

            if ("ADD".equalsIgnoreCase(command)) {
                addEmployee();
            } else if ("VIEW".equalsIgnoreCase(command)) {
                viewEmployees();
            } else if ("UPDATE".equalsIgnoreCase(command)) {
                updateEmployee();
            } else if ("DELETE".equalsIgnoreCase(command)) {
                deleteEmployee();
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

    private void addEmployee() throws IOException, ClassNotFoundException {
        sendMessage("Enter employee name:");
        String name = (String)in.readObject();
        employees.put(employeeIdCounter, name);
        sendMessage("Employee added with ID: " + employeeIdCounter);
        in.readObject();
        employeeIdCounter++;
    }

    private void viewEmployees() throws ClassNotFoundException, IOException {
        if (employees.isEmpty()) {
            sendMessage("No employees found.");
            in.readObject();
        } else {
        	StringBuilder employeeList = new StringBuilder("Employee List:\n");
            employees.forEach((id, name) -> {
            	employeeList.append("ID: " + id + ", Name: " + name + "\n");
            });
            sendMessage(employeeList.toString());
            in.readObject();
        }
    }

    private void updateEmployee() throws IOException, NumberFormatException, ClassNotFoundException {
        sendMessage("Enter employee ID to update:");
        int id = Integer.parseInt((String)in.readObject());
        if (employees.containsKey(id)) {
            sendMessage("Enter new name:");
            String newName = (String)in.readObject();
            employees.put(id, newName);
            sendMessage("Employee updated.");
            in.readObject();
        } else {
            sendMessage("Employee not found.");
            in.readObject();
        }
    }

    private void deleteEmployee() throws IOException, NumberFormatException, ClassNotFoundException {
        sendMessage("Enter employee ID to delete:");
        int id = Integer.parseInt((String)in.readObject());
        if (employees.remove(id) != null) {
            sendMessage("Employee deleted.");
            in.readObject();
        } else {
            sendMessage("Employee not found.");
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
