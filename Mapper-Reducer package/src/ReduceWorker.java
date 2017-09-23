/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import omada17.newyorkapp.*;


public class ReduceWorker extends Worker 
{	
	private Map<String, Data> map = null;
	
	private int max = Constants.MAP_NUM;
	private String clientIP;

		
	public ReduceWorker()	{}
		
	public ReduceWorker(String n)
	{
		this.name = n;
		map = new HashMap<String, Data>();
	}
	
	public ReduceWorker(String n, int p)
	{
		this.name = n;
		port_id = p;
		map = new HashMap<String, Data>();
	}
	
	public void waitForMasterAck()
	{
		int counter = 0;
		
		//Auksanoume to map kata 1 etsi wste na perimenei kai thn entolh tou client gia na ksekinhsei!
		max++;
		
		while (counter < max)
		{
			System.out.println(name+"> "+"Waiting for connection...");
			super.waitForTasksThread();	
			try {
				String message = in.readUTF();
				System.out.println(message);
				//Message from client
				if (message.contains("OKAY"))
				{
					clientIP = in.readUTF();
					System.out.println("Read okay from client with IP: "+clientIP);
					counter++;
				}
				else
				{
					getResults();
					counter++;
				}				
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			System.out.println("------------------------------");
		}
		
		//Calling reducer
		Vector<ArrayList<Checkin>> result = reduce(this.map);
		
		this.close();
		
		//Send results
		sendResults(result);
		
		System.out.println("Reducer exiting...");
		
	}
	

	public void getResults()
	{
		try {
			
			//Waiting for results message
			out.writeUTF(name+"> "+"Waiting for results...");
			out.flush();
			
			@SuppressWarnings("unchecked")
			Map<String, Data> apotelesma = (HashMap<String, Data>) in.readObject();
			for (String key : apotelesma.keySet())
			{
				//An to kleidi uparxei sto map ths classhs
				if (map.containsKey(key))
				{
					Data d = apotelesma.get(key);
					//ta Data den einai idia
					if (!map.get(key).equals(d))
						//antigrafh twn dedomenwn ap'to ena data sto allo
						map.get(key).copyToData(d);
				}
				else
				{
					if ((map.get(key) == null) || (!map.get(key).equals(apotelesma.get(key))))
						map.put(key, apotelesma.get(key));
				}
			}
						
			out.writeUTF(name+"> "+"Map acquired...");
			out.flush();
			
			System.out.println(in.readUTF());
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		System.out.println("Results ok!");
	}
	
	public Vector<ArrayList<Checkin>> reduce(Map<String, Data> m)
	{
		//Find distinct photos
		
		for (Data d: m.values())
		{
			//Get all the photos
			Map<Object, List<Checkin>> ch = d.getList()
					.stream()
					.parallel()
					.collect(Collectors.groupingBy(p -> p.getPhotoLink()));
			
			for (Object o: ch.keySet())
			{
					//System.out.println(o.toString()+" has "+ch.get(o).size());
					//remove all checkins if the list<checkin> has more than 1 element
					for (int i=1; i < ch.get(o).size();i++)
						d.remove(ch.get(o).get(i));
			}
			
			//Data after removing duplicate photo links
			/*System.out.println("Data left with: "+d.size()+" from "+d.count());
			for (int i=0; i<d.size(); i++)
				System.out.println(d.get(i));
			System.out.println("---------------------------------");*/
		}
		
		//Finding top
		
		int top = Constants.topk > m.values().size() ? m.values().size() : Constants.topk;
		//System.out.println("Top = "+top);
		
		Vector<ArrayList<Checkin>> vec = new Vector <ArrayList<Checkin>>();
		vec.setSize(top);
		
		for (int i=0; i<top; i++)
		{
			Collection<Data> set = m.values();
						
			//Vres ti data pou periexei ton megalutero arithmo apo checkins
			Data d = set.stream()
					.map(p -> p)
					.reduce((s,p) -> s.count() > p.count() ? s : p)
					.get();
			
			ArrayList<Checkin> ch = d.getList();
			//Prosthetoume ws "teleutaio checkin" ena checkin pou periexei mono ton arithmo twn sunolikwn checkin ths topothesias
			ch.add(new Checkin(""+d.count()));
			
			//Afairese ap to set to sugkekrimeno data
			set.remove(d);
			
			//prosthhkh enos ap ta topk apotelesmata sto vec
			vec.set(i, ch);
		}
		
		return vec;
	}
	
	public Vector<ArrayList<Checkin>> sendResults(Vector<ArrayList<Checkin>> m)
	{
		Socket requestSocket = null;
		try {
			//dhmiourgia socket
			System.out.println("Trying to contact client...");
			requestSocket = new Socket(InetAddress.getByName(clientIP),Constants.CLIENT_PORT);
			
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			System.out.println(in.readUTF());
			
			//Sending results
			out.writeObject(m);
			out.flush();
			
			System.out.println(in.readUTF());
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (requestSocket != null)
					requestSocket.close();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return m;
	}
	
}
