package com.nimblemind.autoplus;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/22/2017.
 */
final class TitledUri implements Parcelable
{
	public final String title;
	public final Uri uri;

	TitledUri(String title, Uri uri)
	{
		this.title = title;
		this.uri = uri;
	}

	public TitledUri(Parcel in)
	{
		title = in.readString();
		uri = in.readParcelable(Uri.class.getClassLoader());
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(title);
		dest.writeParcelable(uri, flags);
	}

	public static final Creator<TitledUri> CREATOR = new Creator<TitledUri>()
	{
		@Override
		public TitledUri createFromParcel(Parcel in)
		{
			return new TitledUri(in);
		}

		@Override
		public TitledUri[] newArray(int size)
		{
			return new TitledUri[size];
		}
	};
}
