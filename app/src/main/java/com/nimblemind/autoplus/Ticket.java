package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

class Ticket extends Request
{
	public final String partType;

	public Ticket()
	{
		super();
		this.partType = "part1";
	}

	public Ticket(String uid, String autoName, String vin, int year,
				  String engine, String comment, String partType)
	{
		super(uid, autoName, vin, year, engine, comment);
		this.partType = partType;
	}
}
