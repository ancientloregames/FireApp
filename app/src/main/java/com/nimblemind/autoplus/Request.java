package com.nimblemind.autoplus;

import java.io.Serializable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public abstract class Request implements Serializable
{
	public final int id;			// Set on server
	public final String uid;
	public final String sid;		// Id of support member
	public final long timestamp;	// Set on server
	public final String autoName;
	public final String vin;
	public final int year;
	public final String engine;
	public final String comment;
	public final String type;

	public Request()
	{
		id = 0;
		uid = null;
		sid = null;
		autoName = null;
		timestamp = 0;
		vin = null;
		year = 0;
		engine = null;
		comment = null;
		type = getClass().getSimpleName();
	}

	public Request( String uid, String autoName, String vin, int year,
					String engine, String comment)
	{
		this.id = 0;
		this.uid = uid;
		this.sid = "";
		this.autoName = autoName;
		this.timestamp = 0;
		this.vin = vin;
		this.year = year;
		this.engine = engine;
		this.comment = comment;

		this.type = getClass().getSimpleName();
	}
}
