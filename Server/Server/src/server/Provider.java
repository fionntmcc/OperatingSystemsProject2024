package server;

import java.net.*;
public class Provider{
	
	
	
	public static void main(String args[])
	{
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
			
		}
	}
}
