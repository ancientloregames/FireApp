package com.nimblemind.autoplus;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public abstract class Request
{
	public final int id;
	public final String autoName;
	public final long timestamp;
	public final int vin;
	public final int year;
	public final String engine;
	public final String comment;
	// TODO public Uri commentImage;

	public Request()
	{
		id = 0;
		autoName = "";
		timestamp = 0;
		vin = 0;
		year = 0;
		engine = "";
		comment = "";
	}

	public Request(int id, String autoName, long timestamp, int vin, int year,
				   String engine, String comment)
	{
		this.id = id;
		this.autoName = autoName;
		this.timestamp = timestamp;
		this.vin = vin;
		this.year = year;
		this.engine = engine;
		this.comment = comment;
	}
}
