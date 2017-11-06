package com.nimblemind.autoplus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.WorkerThread;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.widget.Toast;

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

	public static int getStatusBarHeight(Resources resources)
	{
		int result;
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		if(resourceId != 0)
		{
			result = resources.getDimensionPixelSize(resourceId);
		}
		else
		{
			DisplayMetrics metrics = resources.getDisplayMetrics();
			result = (int)(24 * metrics.density); // 24dp - standard status bar height
		}
		return result;
	}

	public static boolean checkInternetConnection(Activity activity, boolean withFailToast)
	{
		boolean result = true;
		if (!Utils.isNetworkAvailable(activity))
		{
			if (withFailToast)
			{
				Toast.makeText(activity, activity.getString(R.string.errorNoInternetConnection), Toast.LENGTH_SHORT).show();
			}
			result = false;
		}

		return result;
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
