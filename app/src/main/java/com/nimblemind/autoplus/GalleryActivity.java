package com.nimblemind.autoplus;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/29/2017.
 */

public class GalleryActivity extends AppCompatActivity implements GalleryAdapter.Listener
{
	private final int CODE_INTENT_CAMERA = 100;

	private final int CODE_REQUEST_STORAGE = 101;
	private final int CODE_REQUEST_CAMERA = 102;

	GalleryAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityGallery);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final RecyclerView recycler = findViewById(R.id.recycler);
		final Button cameraButton = findViewById(R.id.buttonTakePhoto);

		cameraButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				tryLaunchCamera();
			}
		});

		int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
		if (rc == PackageManager.PERMISSION_GRANTED)
		{
			populateGallery();
		}
		else
		{
			requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
					CODE_REQUEST_STORAGE, R.string.textRationaleMessageStorage);
		}

		final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);

		adapter = new GalleryAdapter(this);

		recycler.setLayoutManager(layoutManager);
		recycler.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_INTENT_CAMERA && resultCode == RESULT_OK)
		{
			Intent intent = new Intent();
			intent.setData(data.getData());
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			if (requestCode == CODE_REQUEST_STORAGE)
			{
				populateGallery();
			}
			if (requestCode == CODE_REQUEST_CAMERA)
			{
				launchCamera();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemClick(File imageFile)
	{
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(imageFile));
		setResult(RESULT_OK, intent);
		finish();
	}

	private void requestPermission(String permission, final int requestCode, @StringRes int ratianaleText)
	{
		final String[] permissions = new String[] { permission };

		if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
		{
			ActivityCompat.requestPermissions(this, permissions, requestCode);
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle(R.string.textRationaleTitle)
					.setMessage(ratianaleText)
					.setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							ActivityCompat.requestPermissions(GalleryActivity.this, permissions,
									requestCode);
						}
					})
					.setNegativeButton(R.string.textClose, null)
					.create()
					.show();
		}
	}

	private void populateGallery()
	{
		Executors.newSingleThreadExecutor().submit(new Runnable()
		{
			@Override
			public void run()
			{
				final List<File> imageList = getImages();
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						adapter.setItems(imageList);
					}
				});
			}
		});
	}

	private List<File> getImages()
	{
		final List<File> items = new LinkedList<>();
		final ContentResolver contentResolver = getContentResolver();
		final Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media.DATA }, null, null, MediaStore.Images.Media.DATE_ADDED);
		if (cursor != null)
		{
			final int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			while (cursor.moveToNext())
			{
				String path = cursor.getString(dataColumnIndex);
				if(path == null) continue;
				final File imagePath = new File(path);
				if (imagePath.exists())
				{
					items.add(imagePath);
				}
			}
			cursor.close();
		}
		Collections.reverse(items);
		return items;
	}

	private void tryLaunchCamera()
	{
		int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
		if (rc == PackageManager.PERMISSION_GRANTED)
		{
			launchCamera();
		}
		else
		{
			requestPermission(android.Manifest.permission.CAMERA,
					CODE_REQUEST_CAMERA, R.string.textRationaleMessageCamera);
		}
	}

	private void launchCamera()
	{
		Intent intent = new Intent(this, CameraActivity.class);
		startActivityForResult(intent, CODE_INTENT_CAMERA);
	}
}
