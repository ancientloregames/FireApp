package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

class Ticket extends Request
{
	public final PartType partType;
	// TODO public Uri partImage;

	public Ticket()
	{
		super();
		this.partType = PartType.part1;
	}

	public Ticket(int id, String autoName, long timestamp, int vin, int year,
				  String engine, String comment, PartType partType)
	{
		super(id, autoName, timestamp, vin, year, engine, comment);
		this.partType = partType;
	}
}
