package com.nimblemind.autoplus;

import android.text.format.DateFormat;

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
}
