package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.nimblemind.autoplus.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/29/2017.
 */

public class CameraActivity extends AppCompatActivity
{
	private CameraView cameraView;

	private Handler mBackgroundHandler;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		cameraView = findViewById(R.id.camera);

		if (cameraView != null)
		{
			cameraView.addCallback(mCallback);
		}

		findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (cameraView != null) {
					cameraView.takePicture();
				}
			}
		});

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		cameraView.start();
	}

	@Override
	protected void onPause()
	{
		cameraView.stop();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBackgroundHandler != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				mBackgroundHandler.getLooper().quitSafely();
			} else {
				mBackgroundHandler.getLooper().quit();
			}
			mBackgroundHandler = null;
		}
	}

	private void finishWithResult(Uri uri)
	{
		Intent intent = new Intent();
		intent.setData(uri);
		setResult(RESULT_OK, intent);
		finish();
	}

	private CameraView.Callback mCallback = new CameraView.Callback() {
		@Override
		public void onCameraOpened(CameraView cameraView) {}
		@Override
		public void onCameraClosed(CameraView cameraView) {}
		@Override
		public void onPictureTaken(CameraView cameraView, final byte[] data)
		{
			getBackgroundHandler().post(new Runnable() {
				@Override
				public void run() {
					File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
							"picture.jpg");
					OutputStream os = null;
					try {
						os = new FileOutputStream(file);
						os.write(data);
						os.close();
						finishWithResult(Uri.fromFile(file));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (os != null) {
							try {
								os.close();
							} catch (IOException e) {
								// Ignore
							}
						}
					}
				}
			});
		}
	};

	private Handler getBackgroundHandler()
	{
		if (mBackgroundHandler == null)
		{
			HandlerThread thread = new HandlerThread("background");
			thread.start();
			mBackgroundHandler = new Handler(thread.getLooper());
		}
		return mBackgroundHandler;
	}
}
