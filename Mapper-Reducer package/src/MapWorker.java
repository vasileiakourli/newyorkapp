/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

import omada17.newyorkapp.*;


public class MapWorker extends Worker
{
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://83.212.117.76:3306/ds_systems_2016";
	private static final String SQL_START = "SELECT POI, POI_name, latitude, longitude, time, photos FROM checkins";

	//  Database credentials
	private static final String USER = "omada17";
	private static final String PASS = "omada17db";
			
	protected Connection conn = null;
	protected Statement stmt = null;
		
	private ArrayList<Checkin> list = null;
	
	
	MapWorker(String n, int p)
	{
		name = n;
		port_id = p;
	}
	
	public Map<String, Data> map(Coordinates cords[], String date)
	{
		String place ="";
		Map<String, Data> hs = null;
		
		//h nodate einai true otan o xrhsths den dwsei date
		boolean nodate = false;		
		if (date == null ||date.equals(""))
			nodate = true;
		
		
		//Map<Integer, List<Data>> result = null;
		
		try
		{
						
			int cores = Runtime.getRuntime().availableProcessors();
			
			//o sections perilamvanei ta 2 longitude (pou tha kanoume between)
			double [][]sections = new double[cores][2];
			double long1 = cords[0].getLongitude(), long2 = cords[1].getLongitude();
			for (int i=0; i< cores;i++)
			{
				sections[i][0] =  long1 + i*((long2-long1)/cores);
				sections[i][1] =  long1 + (i+1)*((long2-long1)/cores);		    	  
			}
		      
			//ektupwsh coordinates apo kathe kommati
			/*System.out.println("General coordinates: "+cords[0].toString() + " - " +cords[1].toString());
			for (int i=0; i< cores;i++)
			{
				System.out.println((i+1)+" section: ");
				System.out.println(cords[0].getLatitude() +" , "+ sections[i][0]);
				System.out.println(cords[1].getLatitude() +" , "+ sections[i][1]);
			}*/			
			
			//Connecting with database			
			
			//Register driver
			Class.forName(JDBC_DRIVER);
			
			//Open connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connection granted...");
			
			//Create statement
			stmt = conn.createStatement();
			
			String []sql = new String[cores];
			for (int i=0; i<cores; i++)
			{
				//last coordinates
				if (i < cores-1)
					place = "(latitude BETWEEN "+cords[0].getLatitude()+" AND "+cords[1].getLatitude()+" ) AND "
							+ "(longitude >="+sections[i][0]+ " AND longitude < "+sections[i][1]+")";
				else
					place = "(latitude BETWEEN "+cords[0].getLatitude()+" AND "+cords[1].getLatitude()+" ) AND "
							+ "(longitude >="+sections[i][0]+ " AND longitude <= "+sections[i][1]+")"; 
				
				
				sql[i] = SQL_START+ " WHERE "+place;
				
				//if there's a given date
				if (!nodate)
					sql[i] +=" AND "+date;
				
				sql[i] += ";";
			}
			
			//Storing the ResultSets into an ArrayList
			ResultSet rs = null;
			list = new ArrayList<Checkin>();
			
			for (int i=0; i<cores; i++)
			{
				//System.out.println("Query "+(i+1)+": "+sql[i]);
				
				rs = stmt.executeQuery(sql[i]);
				
				while (rs.next())
				{
					list.add(new Checkin(rs.getString("poi"),rs.getString("poi_name"),rs.getDouble("latitude"),
			    			 rs.getDouble("longitude"), rs.getString("time"), rs.getString("photos")));
				}
			}
			

			System.out.println("------------------------------");
			
			
			//Take 1 --> using .map
			
			hs = new HashMap<String, Data>();
			int total =0;
			for (Checkin c: list)
			{
				String key = c.getPOI();
				if (!hs.keySet().contains(key)) //to hashmap exei hdh data me auto to kleidi
				{
					//O arithmos ton checkins pou uparxoun me to dedomeno POI
					long count = list.stream()
							.parallel()
							.map(s -> {
								return s.getPOI();
							})
							.filter(s -> 
							{
								return s.equals(key);
							})
							.count();
					
					Data d = new Data(count);
					d.add(c);
					
					//prosthhkh tou kainouriou data sto hashmap
					hs.put(key, d);
					//System.out.println("POI "+key+" counted "+count+" times.");
					
				}
				else //to hashmap den exei data me to sugkekrimeno key
				{
					Data d = hs.get(key);
					d.add(c);
					hs.put(key, d);
				}
			}
			
			for (String key : hs.keySet())
			{
				total += hs.get(key).count();
				System.out.println("POI "+key+" counted "+hs.get(key).count()+" times.");
			}
			
			System.out.println("Total: "+total);
			System.out.println("------------------------------");
			
			
			
			//Take 2 --> using grouping by
			
			/*total = 0;
			Map<Object, List<Checkin>> map = list
					.stream()
					.parallel()
					.collect(Collectors.groupingBy(p -> p.getPOI()));
			for (Object key : map.keySet())
			{
				total += map.get(key).size();
				System.out.println("POI "+key+" counted "+map.get(key).size()+" times.");
			}
			System.out.println("Total: "+total);*/
			
			
			
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				
				//Close statement
				if (stmt!=null)
					stmt.close();
				
				//Close connection
				if (conn!=null)
					conn.close();
				
				
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		
		return hs;
		
	}
	
	public void notifyMaster()
	{
		try
		{	
			//Mhnuma ston client
			this.out.writeObject("Mapping is completed! The results were sent to reducer...");
			this.out.flush();
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		super.close();
		System.out.println("Everything completed.");
	}
	
	public void waitForTasksThread()
	{
		super.waitForTasksThread();
		
		try 
		{
			//Reading cords
			Object o = in.readObject();
			Coordinates [] c= (Coordinates [])o;
			//Acknowledging
			out.writeUTF(name+"> "+"Coordinates acquired...");
			out.flush();
			
			//Reading date
			String date = (String) in.readObject();
			//Acknowledging
			out.writeUTF(name+"> "+"Date acquired...");
			out.flush();
			
			//Waiting message
			System.out.println(in.readUTF());
			
			//Calling map function
			Map<String, Data> hs = map(c, date);
			
			
			//Notify client
			notifyMaster();

			System.out.println("------------------------------");
			
			//Sending results to reducer
			sendToReducers(hs);
			
			System.out.println("Mapper termination...");
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public Map<String, Data> sendToReducers(Map<String, Data> m)
	{
		Socket requestSocket = null;
		ObjectOutputStream outs = null;
		ObjectInputStream ins = null;
		
		
		
		try {

		    //Thread.sleep(5000); //5 seconds
			
			requestSocket = new Socket(InetAddress.getByName(Constants.REDUCER_ADDRESS), Constants.REDUCER_PORT);
			
			outs = new ObjectOutputStream(requestSocket.getOutputStream());
			ins = new ObjectInputStream(requestSocket.getInputStream());
			
			//steile mhnyma sundeshs
			outs.writeUTF(name+"> "+"with IP: "+requestSocket.getInetAddress().toString()+" and port: "+requestSocket.getLocalPort());
			outs.flush();				
			//emfanise epistrefomeno ack
			System.out.println(ins.readUTF());
			
			//Mhnuma apostolhs
			outs.writeUTF(name+"> "+"Sending map with data...");
			outs.flush();
			
			//Acknowledge
			System.out.println(ins.readUTF());
			
			//Sending map
			outs.writeObject(m);
			outs.flush();
			
			//Acknowledge
			System.out.println(ins.readUTF());
			
			outs.writeUTF(name+"> "+"ending connection...");
			outs.flush();
			
			System.out.println("Ending connection with reducer...");
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if (ins != null)
					ins.close();
				
				if (outs != null)
					outs.close();
				
				if (requestSocket != null)
					requestSocket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return m;
	}
	
	
}
