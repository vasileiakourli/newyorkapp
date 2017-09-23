/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */
package omada17.newyorkapp;

import java.io.Serializable;

public class Checkin implements Serializable
{
	private static final long serialVersionUID = 12345678;
	private String poi;
	private String poi_name;
	private Coordinates cord;
	private String datetime;
	private String photo;
	
	public Checkin(String s)
	{
		poi = s;
		poi_name = datetime = photo = null;
		cord = null;
	}
	
	public Checkin(String p, String pn, double lat, double lon, String dt, String ph)
	{
		poi = p;
		poi_name = pn;
		cord = new Coordinates(lat,lon);
		datetime = dt;
		photo = ph;
		
	}
	
	///Setters
	
	public void setPOI(String s)
	{
		poi = s;
	}
	
	public void setPOIname(String s)
	{
		poi = s;
	}
	
	public void setCoordinates(double d1, double d2)
	{
		cord = new Coordinates(d1,d2);
	}
	
	public void setDatetime(String s)
	{
		datetime = s;
	}
	
	public void setPhotoLink(String s)
	{
		photo = s;
	}
	
	//Getters
	
	public String getPOI()
	{
		return poi;
	}
	
	public String getPOIname()
	{
		return poi_name;
	}
	
	public Coordinates getCoordinates()
	{
		return cord;
	}
	
	public String getDatetime()
	{
		return datetime;
	}
	
	public String getPhotoLink()
	{
		return photo;
	}

	//Methods

	public String toString()
	{
		return "POI: "+poi_name+" ("+poi+")\n"+cord.toString()+"\nTime: "+datetime+"\nPhoto link: "+photo;
	}

	
	public boolean equals(Checkin c)
	{
		return poi.equals(c.poi) && poi_name.equals(c.poi_name) && cord.equals(c.cord) && datetime.equals(c.datetime) && photo.equals(c.photo);
	}
}
