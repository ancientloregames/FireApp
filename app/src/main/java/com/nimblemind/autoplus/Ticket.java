package com.nimblemind.autoplus;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

class Ticket extends Request
{
	public final List<String> spareParts;
	public final String comment;
	public final ArrayList<String> partPhotos;

	public Ticket()
	{
		super();
		spareParts = null;
		comment = null;
		partPhotos = null;
	}

	public Ticket(String uid, String autoBrand, String autoModel, String vin, int year,
				  List<String> spareParts, String comment, ArrayList<String> partPhotos)
	{
		super(uid, autoBrand, autoModel, vin, year);
		this.spareParts = spareParts;
		this.comment = comment;
		this.partPhotos = partPhotos;
	}

	public Ticket(String uid, ArrayList<String> pstPhotos,
				  List<String> spareParts, String comment, ArrayList<String> partPhotos)
	{
		super(uid, pstPhotos);
		this.spareParts = spareParts;
		this.comment = comment;
		this.partPhotos = partPhotos;
	}

	protected Ticket(Parcel in)
	{
		super(in);
		in.readStringList(spareParts = new ArrayList<>());
		comment = in.readString();
		in.readStringList(partPhotos = new ArrayList<>());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		super.writeToParcel(dest, flags);
		dest.writeStringList(spareParts);
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
