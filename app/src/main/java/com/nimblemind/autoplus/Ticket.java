package com.nimblemind.autoplus;

import android.net.Uri;
import android.os.Parcel;
import com.google.firebase.database.Exclude;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

class Ticket extends Request
{
	public final String partType;
	public final String photo;	// Assign in Database
	public final String comment;
	@Exclude
	public final Uri photoUri;	// For internal usage only

	public Ticket()
	{
		super();
		partType = null;
		comment = null;
		photo = null;
		photoUri = null;
	}

	public Ticket(String uid, String autoName, String vin, int year,
				  String comment, String partType, Uri photoUri)
	{
		super(uid, autoName, vin, year);
		this.partType = partType;
		this.photoUri = photoUri;
		this.comment = comment;
		photo = null;
	}

	protected Ticket(Parcel in)
	{
		super(in);
		partType = in.readString();
		photo = in.readString();
		comment = in.readString();
		photoUri = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeString(partType);
		dest.writeString(photo);
		dest.writeString(comment);
		dest.writeParcelable(photoUri, flags);
	}

	public static final Creator<Ticket> CREATOR = new Creator<Ticket>()
	{
		@Override
		public Ticket createFromParcel(Parcel in)
		{
			return new Ticket(in);
		}
		@Override
		public Ticket[] newArray(int size)
		{
			return new Ticket[size];
		}
	};
}
