package com.nimblemind.autoplus;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/19/2017.
 */

public class ImageUploadService extends BasicService
{
	private static final String TAG = "MyUploadService";

	public static final String ACTION_UPLOAD = "action_upload";
	public static final String UPLOAD_COMPLETED = "upload_completed";
	public static final String UPLOAD_ERROR = "upload_error";

	public static final String EXTRA_IMAGES = "extra_images";
	public static final String EXTRA_PATH = "extra_path";
	public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

	private StorageReference storagePhotoRef;

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
			ArrayList<TitledUri> images = intent.getParcelableArrayListExtra(EXTRA_IMAGES);
			String[] path = intent.getStringArrayExtra(EXTRA_PATH);
			for (TitledUri image : images)
			{
				try {
					taskStarted();
					uploadFromUri(image.uri, image.title, path);
				} catch (IOException e) {
					e.printStackTrace();
					taskCompleted();
				}
			}
		}

		return START_REDELIVER_INTENT;
	}

	private void uploadFromUri(final Uri fileUri, final String name, final String[] path) throws IOException
	{
		Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

		int orientation = 1;
		try {
			orientation = Utils.getExifOrientation(getContentResolver(), fileUri);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = Utils.decodeSampledBitmapFromUri(getContentResolver(), fileUri, 1024 , 1024);
		bitmap = Utils.fixImageRotation(bitmap, orientation);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

		// Get a reference to store file at photos/uid/{orderid}.jpg
		StorageReference photoRef = storagePhotoRef;
		for (String folder : path)
		{
			photoRef = photoRef.child(folder);
		}
		photoRef = photoRef.child(name);

		//showProgressNotification(getString(R.string.textUploading), 0, 0);
		Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
		photoRef.putBytes(out.toByteArray())
//				.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
//				{
//					@Override
//					public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
//					{
//						showProgressNotification(getString(R.string.textUploading),
//								taskSnapshot.getBytesTransferred(),
//								taskSnapshot.getTotalByteCount());
//					}
//				})
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
				{
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
					{
						Log.d(TAG, "uploadFromUri:onSuccess");

						Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();

						broadcastUploadFinished(downloadUri, fileUri);
						//showUploadFinishedNotification(downloadUri, fileUri);
						taskCompleted();
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception exception)
					{
						Utils.trySendFabricReport("uploadFromUri:onFailure. fileUri: " + fileUri, exception);

						broadcastUploadFinished(null, fileUri);
						//showUploadFinishedNotification(null, fileUri);
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
				.putExtra(EXTRA_IMAGES, fileUri);
		return LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(broadcast);
	}

	private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri)
	{
		dismissProgressNotification();

		Intent intent = new Intent(this, MainActivity.class)
				.putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
				.putExtra(EXTRA_IMAGES, fileUri)
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
