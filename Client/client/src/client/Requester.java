package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Requester{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	String message;
 	Scanner input;
	Requester(){
		
		input = new Scanner(System.in);
	}
	void run() throws ClassNotFoundException
	{
		try{
			//1. creating a socket to connect to the server
			
			requestSocket = new Socket("127.0.0.1", 2004);
			System.out.println("Connected to localhost in port 2004");
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			//3: Communicating with the server
			
			 System.out.println("Connected to the server.");

	            String serverResponse;
	            while ((serverResponse = (String)in.readObject()) != null) {
	                System.out.println(serverResponse);
	                if (serverResponse.startsWith("Goodbye")) {
	                    break;
	                }

	                sendMessage(input.nextLine());
	            }
			
			
			
						
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	public static void main(String args[]) throws ClassNotFoundException
	{
		Requester client = new Requester();
		client.run();
	}
}
