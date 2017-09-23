/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */
package omada17.newyorkapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;


public class Client extends Thread {
	
	//General variables
	private String worker_address;
	private int worker_port;
	
	//Variables for mapworkers
	private Coordinates[]cords = null;
	private String date = null;
	
	//Variables for reduceworker
	private int client_port = 0;
	
	
	private Socket requestSocket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	
	protected static Vector<ArrayList<Checkin>> vec;
	
	
	//Constructor for reduceworker connection
	public Client(String address, int port, int cport)
	{
		worker_address = address;
		worker_port = port;
		client_port = cport;
	}
	
	//Constructor for mapworker connection
	public Client(String address, int port, Coordinates []c, String d)
	{
		worker_address = address;
		worker_port = port;
		cords = new Coordinates[c.length];
		for (int i =0; i< c.length;i++)
			cords[i] = c[i];

		date = d;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run()
	{
		synchronized(this) 
		{
			try
			{
				//dhmiourgia socket
				requestSocket = new Socket(InetAddress.getByName(worker_address),worker_port);
				
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				in = new ObjectInputStream(requestSocket.getInputStream());

				//Save client's IP
				String ip = requestSocket.getLocalAddress().toString();
				ip = ip.substring(1);

				//steile mhnyma sundeshs
				out.writeUTF("Client with IP: "+ip);
				out.flush();
				
				//emfanise epistrefomeno ack
				System.out.println(in.readUTF());
				
				if (cords != null)
				{
					//Epikoinwnia Client<->MapWorker
					
					//steile ta cords
					out.writeObject(cords);
					out.flush();
					//emfanise epistrefomeno ack
					System.out.println(in.readUTF());
					
					//steile to date
					out.writeObject(date);
					out.flush();
					//emfanise epistrefomeno ack
					System.out.println(in.readUTF());
					
					//steile waiting for reply
					out.writeUTF("Client waiting for reply...");
					out.flush();
					//emfanise apotelesma
					System.out.println((String)in.readObject());
					
				}
				else
				{			
					//Epikoinwnia Client<->ReduceWorker
					
					//Sending trigger
					out.writeUTF("OKAY");
					out.flush();

					out.writeUTF(ip);
					out.flush();


					//Waiting for results
					//in.readObject();
					
				}
				
				System.out.println("Client termination...");
				
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally
			{
				//Close previous connections
				this.close();
				
				//Wait for new connection if we have Client <-> Reducer
				if (cords == null)
				{

					ServerSocket providerSocket = null;
					Socket connection = null;
					
					//dhmiourgia socket
					try 
					{
						providerSocket = new ServerSocket(client_port,10);
						System.out.println("Waiting for connection....");
						connection = providerSocket.accept();
						
						out = new ObjectOutputStream(connection.getOutputStream());
						in = new ObjectInputStream(connection.getInputStream());
						
						System.out.println("Waiting for results...");
						out.writeUTF("Client: Waiting for results...");
						out.flush();
						
						vec =(Vector<ArrayList<Checkin>>)in.readObject();

						
						System.out.println("Ending connection...");
						out.writeUTF("Client: Ending connection...");
						out.flush();
										
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					finally
					{
						try 
						{
							if (in != null)
								in.close();
							
							if (out != null)
								out.close();
							
							if (connection != null)
								connection.close();
							
							if (providerSocket != null)
								providerSocket.close();
							
							System.out.println("------------------------------");
						}
						catch(IOException e)
						{
							e.printStackTrace();
						}
					}			
					
					
					
				}
			}
			
		}
	}

	
	
	public void close()
	{
		try 
		{
			if (in != null)
				in.close();
			
			if (out != null)
				out.close();
			
			if (requestSocket != null)
				requestSocket.close();
			
			System.out.println("------------------------------");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
}