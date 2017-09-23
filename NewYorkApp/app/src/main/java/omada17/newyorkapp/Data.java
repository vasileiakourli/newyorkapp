/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */

package omada17.newyorkapp;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable
{
	private static final long serialVersionUID = 1234567;
	private ArrayList<Checkin> checkins;
	private int count;
	
	public Data()
	{
		checkins = new ArrayList<Checkin>();
		count = 0;
	}
	
	public Data(Long l)
	{
		checkins = new ArrayList<Checkin>();
		count = l.intValue();
	}
	
	//ArrayList methods
	public void add(Checkin c)
	{
		checkins.add(c);
	}
	
	public Checkin get(int i)
	{
		return checkins.get(i);
	}
		
	public boolean remove(Checkin c)
	{
		return checkins.remove(c);	
	}
		
	public int size()
	{
		return checkins.size();
	}
	
	public ArrayList<Checkin> getList()
	{
		return checkins;
	}
	
	public void setList(ArrayList<Checkin> a)
	{
		checkins = a;
	}
	
	public void copyToData(Data d)
	{
		int i = 0;
		for(Checkin c: d.checkins)
		{
			//An to Data den periexei auto to checkin
			if (!this.checkins.contains(c))
			{
				this.checkins.add(c);
				i++;
			}
		}
		count += i;
	}
	
	
	//Counter methods
	
	public int count()
	{
		return count;
	}
	
	public boolean contains(Checkin c)
	{
		if (c==null)
			return false;
		return checkins.contains(c);
	}

	public boolean equals(Data d)
	{
		if (count != d.count)
			return false;
		
		if (checkins.size() != d.checkins.size())
			return false;
		
		for (int i=0; i<checkins.size();i++)
			if (!checkins.get(i).equals(d.checkins.get(i)))
				return false;
		return true;
				
	}
	
	
	
	
	
}
