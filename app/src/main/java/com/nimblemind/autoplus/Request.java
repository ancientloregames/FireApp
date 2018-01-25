package com.nimblemind.autoplus;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public abstract class Request implements Parcelable
{
	public final int id;			// Set on server
	public final String uid;
	public final String sid;		// Id of support member. Do it empty String for support search of open Requests!
	public final long timestamp;	// Set on server
	public final String autoBrand;
	public final String autoModel;
	public final String vin;
	public final int year;
	public final String type;
	public final ArrayList<String> ptsPhotos;
	public final int totalMsgs;			// Set on server
	public final int unreadUsrMsgs;		// Set on server
	public final int unreadSupMsgs;		// Set on server

	public Request()
	{
		id = 0;
		uid = null;
		sid = null;
		autoBrand = null;
		autoModel = null;
		timestamp = 0;
		vin = null;
		year = 0;
		type = getClass().getSimpleName();
		ptsPhotos = null;
		totalMsgs = 0;
		unreadUsrMsgs = 0;
		unreadSupMsgs = 0;
	}

	public Request(String uid, String autoBrand, String autoModel, String vin, int year)
	{
		this.id = 0;
		this.uid = uid;
		this.sid = "";
		this.autoBrand = autoBrand;
		this.autoModel = autoModel;
		this.timestamp = 0;
		this.vin = vin;
		this.year = year;
		this.type = getClass().getSimpleName();
		this.ptsPhotos = null;
		this.totalMsgs = 0;
		this.unreadUsrMsgs = 0;
		this.unreadSupMsgs = 0;
	}

	public Request(String uid, ArrayList<String> ptsPhotos)
	{
		this.id = 0;
		this.uid = uid;
		this.sid = "";
		this.autoBrand = null;
		this.autoModel = null;
		this.timestamp = 0;
		this.vin = null;
		this.year = 0;
		this.type = getClass().getSimpleName();
		this.ptsPhotos = ptsPhotos;
		this.totalMsgs = 0;
		this.unreadUsrMsgs = 0;
		this.unreadSupMsgs = 0;
	}

	protected Request(Parcel in)
	{
		id = in.readInt();
		uid = in.readString();
		sid = in.readString();
		timestamp = in.readLong();
		autoBrand = in.readString();
		autoModel = in.readString();
		vin = in.readString();
		year = in.readInt();
		type = in.readString();
		in.readStringList(ptsPhotos = new ArrayList<>());
		totalMsgs = in.readInt();
		unreadUsrMsgs = in.readInt();
		unreadSupMsgs = in.readInt();
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
		dest.writeString(autoBrand);
		dest.writeString(autoModel);
		dest.writeString(vin);
		dest.writeInt(year);
		dest.writeString(type);
		dest.writeStringList(ptsPhotos);
		dest.writeInt(totalMsgs);
		dest.writeInt(unreadUsrMsgs);
		dest.writeInt(unreadSupMsgs);
	}
}
