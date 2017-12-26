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
	public final String storageFolder;	// Set on server
	public final ArrayList<String> ptsPhotos;

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
		storageFolder = null;
		ptsPhotos = null;
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
		this.storageFolder = null;
		this.ptsPhotos = null;
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
		this.storageFolder = null;
		this.ptsPhotos = ptsPhotos;
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
		storageFolder = in.readString();
		in.readStringList(ptsPhotos = new ArrayList<>());
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
		dest.writeString(storageFolder);
		dest.writeStringList(ptsPhotos);
	}
}
