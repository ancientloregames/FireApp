package com.nimblemind.autoplus;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

class Ticket extends Request
{
	public final String partType;
	public final String comment;
	public List<String> partPhotos;

	public Ticket()
	{
		super();
		partType = null;
		comment = null;
		partPhotos = null;
	}

	public Ticket(String uid, String autoName, String vin, int year,
				  String partType, String comment, List<String> partPhotos)
	{
		super(uid, autoName, vin, year);
		this.partType = partType;
		this.comment = comment;
		this.partPhotos = partPhotos;
	}

	public Ticket(String uid, List<String> pstPhotos, String partType, String comment, List<String> partPhotos)
	{
		super(uid, pstPhotos);
		this.partType = partType;
		this.comment = comment;
		this.partPhotos = partPhotos;
	}

	protected Ticket(Parcel in)
	{
		super(in);
		partType = in.readString();
		comment = in.readString();
		partPhotos = new ArrayList<>();
		in.readStringList(partPhotos);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeString(partType);
		dest.writeString(comment);
		dest.writeStringList(partPhotos);
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
