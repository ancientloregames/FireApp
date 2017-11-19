package com.nimblemind.autoplus;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/19/2017.
 */

public class ImageUploadService extends BasicService
{
	private static final String TAG = "MyUploadService";

	public static final String ACTION_UPLOAD = "action_upload";
	public static final String UPLOAD_COMPLETED = "upload_completed";
	public static final String UPLOAD_ERROR = "upload_error";

	public static final String EXTRA_FILE_URI = "extra_file_uri";
	public static final String EXTRA_FILE_NAME = "extra_file_name";
	public static final String EXTRA_FILE_PATH = "extra_file_path";
	public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

	private StorageReference storagePhotoRef;

	private final int maxSize = 1270;

	@Override
	public void onCreate()
	{
		super.onCreate();

		storagePhotoRef = FirebaseStorage.getInstance().getReference("photos");
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.d(TAG, "onStartCommand:" + intent + ":" + startId);
		if (ACTION_UPLOAD.equals(intent.getAction()))
		{
			Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
			String name = intent.hasExtra(EXTRA_FILE_NAME)
					? intent.getStringExtra(EXTRA_FILE_NAME)
					: String.valueOf(System.currentTimeMillis());
			String[] path = intent.getStringArrayExtra(EXTRA_FILE_PATH);
			try
			{
				uploadFromUri(fileUri, name, path);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return START_REDELIVER_INTENT;
	}

	private void uploadFromUri(final Uri fileUri, final String name, final String[] path) throws IOException
	{
		Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

		taskStarted();
		showProgressNotification(getString(R.string.textUnloading), 0, 0);

		Bitmap bitmap = resizeImage(MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

		// Get a reference to store file at photos/uid/{orderid}.jpg
		StorageReference photoRef = storagePhotoRef;
		for (String folder : path)
		{
			photoRef = photoRef.child(folder);
		}
		photoRef = photoRef.child(name);

		Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
		photoRef.putBytes(out.toByteArray()).
				addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
					{
						showProgressNotification(getString(R.string.textUnloading),
								taskSnapshot.getBytesTransferred(),
								taskSnapshot.getTotalByteCount());
					}
				})
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						Log.d(TAG, "uploadFromUri:onSuccess");

						Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

						broadcastUploadFinished(downloadUri, fileUri);
						showUploadFinishedNotification(downloadUri, fileUri);
						taskCompleted();
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception exception)
					{
						Log.w(TAG, "uploadFromUri:onFailure", exception);

						broadcastUploadFinished(null, fileUri);
						showUploadFinishedNotification(null, fileUri);
						taskCompleted();
					}
				});
	}

	private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri)
	{
		boolean success = downloadUrl != null;

		String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

		Intent broadcast = new Intent(action)
				.putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
				.putExtra(EXTRA_FILE_URI, fileUri);
		return LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(broadcast);
	}

	private Bitmap resizeImage(Bitmap bitmap)
	{
		Bitmap result = bitmap;
		if (bitmap.getHeight() > maxSize || bitmap.getWidth() > maxSize)
		{
			if (bitmap.getHeight() > bitmap.getWidth())
			{
				float aspectRatio = bitmap.getWidth() / (float) bitmap.getHeight();
				result = Bitmap.createScaledBitmap(bitmap, (int) (maxSize * aspectRatio), maxSize, false);
			}
			else
			{
				float aspectRatio = bitmap.getHeight() / (float) bitmap.getWidth();
				result = Bitmap.createScaledBitmap(bitmap, maxSize, (int) (maxSize * aspectRatio), false);
			}
		}

		return result;
	}

	private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri)
	{
		dismissProgressNotification();

		Intent intent = new Intent(this, MainActivity.class)
				.putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
				.putExtra(EXTRA_FILE_URI, fileUri)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean success = downloadUrl != null;
		String caption = success ? getString(R.string.textUploadSuccess) : getString(R.string.textUploadFailure);
		showFinishedNotification(caption, intent, success);
	}

	public static IntentFilter getIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPLOAD_COMPLETED);
		filter.addAction(UPLOAD_ERROR);

		return filter;
	}
}
