package server;

public class Employee {

	private String id, name, email, password;
	private Department dept;
	private Role role;
	
	public Employee(String id,
			String name, 
			String email,
			String password,
			Department dept,
			Role role) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.dept = dept;
		this.role = role;
	}
	
	public String id() {
		return id;
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
				"Role: " + role() + "\n" +
				"Password: " + password() + "\n"
				);
	}
	

}
