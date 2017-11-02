package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;


public abstract class NewRequestActivity<MODEL extends Request> extends AppCompatActivity
{
	private static final int RC_TAKE_PICTURE = 105;

	protected String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_request);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(getActivityTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ViewGroup formContainer = findViewById(R.id.form);
		LayoutInflater.from(this).inflate(getFormLayoutId(), formContainer);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		if (savedInstanceState == null)
		{
			onFirstCreate();
		}
	}

	private void onFirstCreate()
	{
		Intent intent = getIntent();
		if (intent != null)
		{
			uid = intent.getStringExtra("uid");
			if (uid == null)
			{
				throw new RuntimeException("The Uid must be passed in order to create new ticket");
			}

			MODEL template = (MODEL) intent.getSerializableExtra("template");
			if (template != null)
			{
				populateWithTemplate(template);
			}
		}
		else throw new RuntimeException("The Uid must be passed in order to create new ticket");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == RC_TAKE_PICTURE)
			{
				Uri imageUri = data.getData();
				if (imageUri != null)
				{
					onRecivedImage(imageUri);
				}
			}
		}
		else
		{
			if (requestCode == RC_TAKE_PICTURE)
			{
				Toast.makeText(this, getString(R.string.errorPhotoNotRecieved), Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void submit(MODEL request, Uri imageUri)
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra("request", request);
		resultIntent.putExtra("photo", imageUri);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	protected void openGallery()
	{
		Intent intent = new Intent(this, GalleryActivity.class);
		startActivityForResult(intent, RC_TAKE_PICTURE);
	}

	@LayoutRes
	protected abstract int getFormLayoutId();

	@StringRes
	protected abstract int getActivityTitle();

	protected abstract void onRecivedImage(Uri imageUri);

	protected abstract void populateWithTemplate(@NonNull MODEL template);

	protected abstract void tryCreateRequest();
}
