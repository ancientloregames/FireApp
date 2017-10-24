package com.nimblemind.autoplus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import android.text.format.DateFormat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public class Utils
{
	public static String getDate(long time)
	{
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTimeInMillis(time);
		String date = DateFormat.format("dd-MM-yyyy", cal).toString();
		return date;
	}

	// Check if network is available
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	// ping the firebase server to check if internet is really working or not
	@WorkerThread
	public static boolean isInternetWorking()
	{
		boolean result = false;
		try
		{
			URL url = new URL("https://firebase.google.com/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);
			connection.connect();
			result = connection.getResponseCode() == 200;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
