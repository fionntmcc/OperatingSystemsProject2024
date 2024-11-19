package server;

public record Employee(String name, 
		String id,
		String email,
		String password,
		Department dept,
		Role role) {

}
