package com.nimblemind.autoplus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/19/2017.
 */

public abstract class BasicService extends Service
{
	static final int PROGRESS_NOTIFICATION_ID = 0;
	static final int FINISHED_NOTIFICATION_ID = 1;

	private static final String TAG = "BasicService";
	private int mumTasks = 0;

	public void taskStarted()
	{
		changeNumberOfTasks(1);
	}

	public void taskCompleted()
	{
		changeNumberOfTasks(-1);
	}

	private synchronized void changeNumberOfTasks(int delta)
	{
		Log.d(TAG, "changeNumberOfTasks:" + mumTasks + ":" + delta);
		mumTasks += delta;

		if (mumTasks <= 0)
		{
			Log.d(TAG, "stopping");
			stopSelf();
		}
	}

	protected void showProgressNotification(String caption, long completedUnits, long totalUnits)
	{
		int percentComplete = 0;
		if (totalUnits > 0)
		{
			percentComplete = (int) (100 * completedUnits / totalUnits);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_file_upload_white_24dp)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(caption)
				.setProgress(100, percentComplete, false)
				.setOngoing(true)
				.setAutoCancel(false);

		NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
	}

	protected void showFinishedNotification(String caption, Intent intent, boolean success)
	{
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		int icon = success ? R.drawable.ic_check_white_24 : R.drawable.ic_error_white_24dp;

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setSmallIcon(icon)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(caption)
				.setAutoCancel(true)
				.setContentIntent(pendingIntent);

		NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		manager.notify(FINISHED_NOTIFICATION_ID, builder.build());
	}

	protected void dismissProgressNotification()
	{
		NotificationManager manager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		manager.cancel(PROGRESS_NOTIFICATION_ID);
	}
}
