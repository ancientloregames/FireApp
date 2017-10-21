package com.nimblemind.autoplus;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;


public class NewRequestActivity extends AppCompatActivity
{
	private static final int RC_TAKE_PICTURE = 105;

	// permission request codes need to be < 256
	private static final int CODE_REQUEST_CAMERA = 201;

	private AutoCompleteTextView nameView;
	private TextView vinView;
	private TextView yearView;
	private TextView engineView;
	private AutoCompleteTextView partView;
	private TextView commentView;
	private ImageView imageView;

	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_request);

		nameView = findViewById(R.id.textName);
		vinView = findViewById(R.id.textVin);
		yearView = findViewById(R.id.textYear);
		engineView = findViewById(R.id.textEngine);
		partView = findViewById(R.id.textPart);
		commentView = findViewById(R.id.textComment);
		imageView = findViewById(R.id.image);

		final String uid;
		Intent intent = getIntent();
		if (intent != null)
		{
			uid = intent.getStringExtra("uid");

			Request template = (Request) intent.getSerializableExtra("template");
			if (template != null)
			{
				populateWithTemplate(template);
			}
		}
		else throw new RuntimeException("The Uid must be passed in order to create new ticket");

		DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
		populateAutoCompleteList(dbRef.child("AutoNames"), nameView);
		populateAutoCompleteList(dbRef.child("parts"), partView);

		findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				submit(uid);
			}
		});

		imageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				tryLaunchCamera();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == RC_TAKE_PICTURE)
			{
				imageUri = data.getData();
				if (imageUri != null)
				{
					Glide.with(this)
							.load(imageUri)
							.into(imageView);
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

	private void tryLaunchCamera()
	{
		int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
		if (rc == PackageManager.PERMISSION_GRANTED)
		{
			launchCamera();
		}
		else
		{
			requestCameraPermission();
		}
	}

	private void requestCameraPermission()
	{
		final String[] permissions = new String[] { android.Manifest.permission.CAMERA };

		if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
		{
			ActivityCompat.requestPermissions(this, permissions, CODE_REQUEST_CAMERA);
		}
		else
		{
			new AlertDialog.Builder(this)
					.setTitle(R.string.textRationaleTitle)
					.setMessage(R.string.textRationaleMessageCamera)
					.setPositiveButton(R.string.textOK, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialogInterface, int i)
						{
							ActivityCompat.requestPermissions(NewRequestActivity.this, permissions,
									CODE_REQUEST_CAMERA);
						}
					})
					.setNegativeButton(R.string.textClose, null)
					.create()
					.show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == CODE_REQUEST_CAMERA
				&& grantResults.length != 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED)
		{
			launchCamera();
		}
	}

	private void launchCamera()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, RC_TAKE_PICTURE);
	}

	private void openGallery()
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, RC_TAKE_PICTURE);
	}

	private void submit(String uid)
	{
		String name = nameView.getText().toString();
		String part = partView.getText().toString();

		int year;
		try {
			year = Integer.valueOf(yearView.getText().toString());
		} catch(NumberFormatException | NullPointerException e) {
			yearView.setError(getString(R.string.errorInvalidField));
			return;
		}

		if (validate(name, part, year))
		{
			String vin = vinView.getText().toString();
			String engine = engineView.getText().toString();
			String comment = commentView.getText().toString();

			Ticket ticket = new Ticket(uid, name, vin, year, engine, comment, part);
			Intent resultIntent = new Intent();
			resultIntent.putExtra("request", ticket);
			resultIntent.putExtra("photo", imageUri);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
	}

	private void populateWithTemplate(@NonNull Request template)
	{
		nameView.setText(template.autoName);
		nameView.setInputType(InputType.TYPE_NULL);
		vinView.setText(String.valueOf(template.vin));
		vinView.setInputType(InputType.TYPE_NULL);
		yearView.setText(String.valueOf(template.year));
		yearView.setInputType(InputType.TYPE_NULL);
		engineView.setText(template.engine);
		engineView.setInputType(InputType.TYPE_NULL);
		commentView.setText(template.comment);
		commentView.setInputType(InputType.TYPE_NULL);
		commentView.setFocusable(false);
	}

	private boolean validate(String name, String part, int year)
	{
		boolean result = true;

		if (name.isEmpty())
		{
			nameView.setError(getString(R.string.errorFieldRequired));
			result = false;
		}

		if (part.isEmpty())
		{
			partView.setError(getString(R.string.errorFieldRequired));
			result = false;
		}

		if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR))
		{
			yearView.setError(getString(R.string.errorInvalidField));
			result = false;
		}

		if (imageUri == null)
		{
			Toast.makeText(this, getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			result = false;
		}

		return result;
	}

	private void populateAutoCompleteList(@NonNull DatabaseReference dbRef, @NonNull final AutoCompleteTextView view)
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
