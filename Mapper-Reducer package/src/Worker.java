/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

import java.io.*;
import java.net.*;


public class Worker
{
	
	ServerSocket providerSocket = null;
	Socket connection = null;
	
	ObjectOutputStream out = null;
	ObjectInputStream in = null;
	
	int port_id = 0;
	String name = "worker";
	
	public Worker()	{}
	
	
	public Worker(String n, int p)
	{
		this.name = n;
		port_id = p;
	}
	
	public void initialize()
	{ 
		try	{
			providerSocket = new ServerSocket(port_id,10);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void waitForTasksThread()
	{
		try {	
			
			connection = providerSocket.accept();	
						
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
			
			//Diavazei mhnuma
			System.out.println(in.readUTF());
			
			//Acknowledge
			out.writeUTF(name+"> "+"IP: "+connection.getLocalAddress().toString());
			out.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public void close()
	{
		try {
			if (in != null)
				in.close();
			
			if (out != null)
				out.close();
			
			if (connection != null)
				connection.close();
			
			if (providerSocket != null)
				providerSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
}