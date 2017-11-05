package com.nimblemind.autoplus;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class App extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		if (BuildConfig.WITH_FABRIC)
		{
			Fabric.with(this, new Crashlytics());
		}
	}
}
