package com.nimblemind.autoplus;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
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

	private GalleryAdapter adapter;

	private Uri photoUri;

	@Override
	protected void onCreate(@Nullable Bundle prevState)
	{
		super.onCreate(prevState);
		setContentView(R.layout.activity_gallery);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityGallery);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (prevState != null)
		{
			photoUri = prevState.getParcelable("photoUri");
		}

		final RecyclerView recycler = findViewById(R.id.recycler);
		final Button cameraButton = findViewById(R.id.buttonTakePhoto);

		cameraButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchCamera();
			}
		});

		int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
		if (rc == PackageManager.PERMISSION_GRANTED)
		{
			populateGallery();
		}
		else
		{
			requestPermission(CODE_REQUEST_STORAGE, R.string.textRationaleMessageStorage,
					android.Manifest.permission.READ_EXTERNAL_STORAGE);
		}

		final RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);

		adapter = new GalleryAdapter(this);

		recycler.setLayoutManager(layoutManager);
		recycler.setAdapter(adapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelable("photoUri", photoUri);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_INTENT_CAMERA && resultCode == RESULT_OK)
		{
			Intent intent = new Intent();
			intent.setData(photoUri);
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

	private void requestPermission(final int requestCode, @StringRes int ratianaleText, @NonNull final String... permissions)
	{
		if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]))
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
			requestPermission(CODE_REQUEST_CAMERA, R.string.textRationaleMessageCamera,
					android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
	}

	private void launchCamera()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File path = new File(getFilesDir().getPath());
		File file = new File(path, String.valueOf(System.currentTimeMillis()) +".jpg");
		try {
			path.mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			Utils.trySendFabricReport("Error creating file: " + file.getPath(), e);
		}
		photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			ClipData clip = ClipData.newUri(getContentResolver(), "A photo", photoUri);
			intent.setClipData(clip);

			List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

			for (ResolveInfo resolveInfo : resInfoList)
			{
				String packageName = resolveInfo.activityInfo.packageName;
				grantUriPermission(packageName, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			}
		}

		if (intent.resolveActivity(getPackageManager()) != null)
		{
			startActivityForResult(intent, CODE_INTENT_CAMERA);
		}
	}
}
