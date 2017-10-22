package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public abstract class NewRequestActivity<MODEL extends Request> extends AppCompatActivity
{
	private static final int RC_TAKE_PICTURE = 105;

	protected String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
		{
			onFirstCreate();
		}
	}

	private void onFirstCreate()
	{
		setContentView(R.layout.activity_new_request);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ViewGroup formContainer = findViewById(R.id.form);
		LayoutInflater.from(this).inflate(getFormLayoutId(), formContainer);

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

	protected void tryOpenGallery()
	{
		// FIXME Сделать собственную галерею с вызовом камеры
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, RC_TAKE_PICTURE);
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		startActivityForResult(intent, RC_TAKE_PICTURE);
	}

	@LayoutRes
	protected abstract int getFormLayoutId();

	protected abstract void onRecivedImage(Uri imageUri);

	protected abstract void populateWithTemplate(@NonNull MODEL template);

	protected abstract void tryCreateRequest();

	protected void populateAutoCompleteList(@NonNull DatabaseReference dbRef, @NonNull final AutoCompleteTextView view)
	{
		dbRef.addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				List<String> items = FirebaseUtils.snapshotToList(dataSnapshot, String.class);
				ArrayAdapter<String> arrayAdapter =
						new ArrayAdapter<>(NewRequestActivity.this, android.R.layout.select_dialog_item, items);
				view.setAdapter(arrayAdapter);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				databaseError.toException().printStackTrace();
			}
		});
	}
}
