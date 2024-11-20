package server;

public class Employee {
	
	private static int idCounter = 0;
	private int idNum;
	private String name, email, password;
	private Department dept;
	private Role role;
	
	public Employee(String name, 
			String email,
			String password,
			Department dept,
			Role role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.dept = dept;
		this.role = role;
		this.idNum = idCounter++;
	}
	
	public String id() {
		return ("EMP" + idNum);
	}
	
	public String name() {
		return name;
	}

	public String email() {
		return email;
	}

	public String password() {
		return password;
	}

	public Department dept() {
		return dept;
	}

	public Role role() {
		return role;
	}
	
	@Override
	public String toString() {
		return ("Employee id: " + id() + "\n" +
				"Name: " + name() + "\n" +
				"Email: " + email() + "\n" +
				"Dept: " + dept() + "\n" +
				"Role: " + id() + "\n" +
				"Password: " + password() + "\n"
				);
	}
	

}
