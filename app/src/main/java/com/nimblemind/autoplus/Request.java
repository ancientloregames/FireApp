package com.nimblemind.autoplus;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public abstract class Request implements Parcelable
{
	public final int id;			// Set on server
	public final String uid;
	public final String sid;		// Id of support member
	public final long timestamp;	// Set on server
	public final String autoName;
	public final String vin;
	public final int year;
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

		type = getClass().getSimpleName();
	}

	public Request(String uid, String autoName, String vin, int year)
	{
		this.id = 0;
		this.uid = uid;
		this.sid = "";
		this.autoName = autoName;
		this.timestamp = 0;
		this.vin = vin;
		this.year = year;

		this.type = getClass().getSimpleName();
	}

	protected Request(Parcel in)
	{
		id = in.readInt();
		uid = in.readString();
		sid = in.readString();
		timestamp = in.readLong();
		autoName = in.readString();
		vin = in.readString();
		year = in.readInt();
		type = in.readString();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(id);
		dest.writeString(uid);
		dest.writeString(sid);
		dest.writeLong(timestamp);
		dest.writeString(autoName);
		dest.writeString(vin);
		dest.writeInt(year);
		dest.writeString(type);
	}
}
