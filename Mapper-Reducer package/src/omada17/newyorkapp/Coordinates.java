/*
 * omada17
 * Kassari Anastasia 3130088
 * Kourli Vasileia 3130101
 * Stavrinos Michail Taxiarchis 3130193
 */
package omada17.newyorkapp;

import java.io.Serializable;

public class Coordinates implements Serializable
{
	
	private static final long serialVersionUID = 01234567;
	private double latitude;
	private double longitude;
	
	public Coordinates(double lat, double lon)
	{
		latitude = lat;
		longitude = lon;
	}
	
	//Setters
	
	public void setLatitude(double l)
	{
		latitude = l;
	}
	
	public void setLongitude(double l)
	{
		longitude = l;
	}
	
	
	//Getters
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}

	//Methods
	
	public boolean containsLatitude(double lat_min, double lat_max)
	{
		return ((latitude>= lat_min) && (latitude<=lat_max));
	}
	
	public boolean containsLongitude(double lon_min, double lon_max)
	{
		return ((longitude>= lon_min) && (longitude<=lon_max));
	}
	
	public String toString()
	{
		return "Coordinates: "+latitude+" , "+longitude;
	}
	
	public boolean equals(Coordinates c)
	{
		return (latitude == c.latitude) && (longitude == c.longitude);
	}
}

