package com.nimblemind.autoplus;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseException;
import io.fabric.sdk.android.Fabric;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import static io.fabric.sdk.android.Fabric.TAG;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public class Utils
{
	public final static int FORMAT_UPPER_CASE = 1;
	public final static int FORMAT_LOWER_CASE = 2;
	public final static int FORMAT_CAP_SENTENCE = 3;

	public static void openImage(@NonNull Context context, File file)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
		intent.setDataAndType(uri,"image/*");
		if (intent.resolveActivity(context.getPackageManager()) != null)
		{
			context.startActivity(intent);
		}
		else Toast.makeText(context, context.getString(R.string.textNoImageViewer) , Toast.LENGTH_SHORT).show();
	}

	public static Bitmap decodeSampledBitmapFromUri(ContentResolver resolver, Uri uri, int reqWidth, int reqHeight) throws IOException
	{
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// First decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		InputStream input = null;
		try {
			input = resolver.openInputStream(uri);
			BitmapFactory.decodeStream(input, null, options);
		} finally {
			closeQuietly(input);
		}
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap;
		try {
			input = resolver.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(input, null, options);
		} finally {
			closeQuietly(input);
		}
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth)
		{
			inSampleSize *= 2;
		}

		return inSampleSize;
	}

	public static int getExifOrientation(ContentResolver resolver, Uri selectedImage) throws IOException
	{
		ExifInterface ei;
		InputStream input = null;
		try {
			input = resolver.openInputStream(selectedImage);
			ei = new ExifInterface(input);
		} finally {
			closeQuietly(input);
		}

		return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	}

	public static Bitmap fixImageRotation(Bitmap img, int orientation)
	{
		switch (orientation)
		{
			case ExifInterface.ORIENTATION_ROTATE_90:
				return rotateImage(img, 90);
			case ExifInterface.ORIENTATION_ROTATE_180:
				return rotateImage(img, 180);
			case ExifInterface.ORIENTATION_ROTATE_270:
				return rotateImage(img, 270);
			default:
				return img;
		}
	}

	public static Bitmap rotateImage(Bitmap img, int degree)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
		img.recycle();
		return rotatedImg;
	}

	private static void closeQuietly(Closeable closeable)
	{
		if (closeable != null)
		{
			try
			{
				closeable.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static String getDate(long time, String format)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		String date = DateFormat.format(format, cal).toString();
		return date;
	}

	public static String formatText(@NonNull String text, int format)
	{
		if (text.isEmpty())
			return text;

		switch (format)
		{
			case FORMAT_UPPER_CASE:
				return text.toUpperCase();
			case FORMAT_LOWER_CASE:
				return text.toLowerCase();
			case FORMAT_CAP_SENTENCE:
				return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
			default: return text;
		}
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

	public static void trySendFabricReport(@NonNull String message, @Nullable Throwable exception)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.logException(new Exception(message, exception));
		}
		Log.w(TAG, message, exception);
	}

	protected static void handleFirebaseErrorWithToast(@NonNull Context context, @NonNull String message, @Nullable Throwable exception)
	{
		if (Fabric.isInitialized())
		{
			Crashlytics.logException(new Exception(message, exception));
		}
		Log.w(TAG, message, exception);

		String text;
		if (exception instanceof FirebaseAuthUserCollisionException)
		{
			text = context.getString(R.string.errorAuthCollision);
		}
		else if (exception instanceof FirebaseAuthInvalidUserException)
		{
			text = context.getString(R.string.errorAuthInvalidUser);
		}
		else if (exception instanceof FirebaseAuthRecentLoginRequiredException)
		{
			text = context.getString(R.string.errorAuthRecentLogin);
		}
		else if (exception instanceof FirebaseAuthEmailException)
		{
			text = context.getString(R.string.errorAuthEmail);
		}
		else if (exception instanceof FirebaseAuthInvalidCredentialsException)
		{
			text = context.getString(R.string.errorAuthInvalidCredentials);
		}
		else if (exception instanceof DatabaseException)
		{
			text = context.getString(R.string.errorAythDatabase);
		}
		else
		{
			text = context.getString(R.string.errorAuthGeneral);
		}
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
