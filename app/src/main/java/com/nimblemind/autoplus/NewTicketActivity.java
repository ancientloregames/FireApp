package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import java.util.Calendar;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/22/2017.
 */

public class NewTicketActivity extends NewRequestActivity<Ticket>
{
	private final int INTENT_AUTO_NAME = 100;
	private final int INTENT_SPARE_PART = 101;

	private TextView nameView;
	private TextView vinView;
	private TextView yearView;
	private TextView engineView;
	private TextView partView;
	private TextView commentView;
	private ImageView imageView;

	private Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		nameView = findViewById(R.id.textName);
		vinView = findViewById(R.id.textVin);
		yearView = findViewById(R.id.textYear);
		engineView = findViewById(R.id.textEngine);
		partView = findViewById(R.id.textPart);
		commentView = findViewById(R.id.textComment);
		imageView = findViewById(R.id.image);

		nameView.setInputType(InputType.TYPE_NULL);
		partView.setInputType(InputType.TYPE_NULL);

		nameView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openList("AutoNames", INTENT_AUTO_NAME);
			}
		});

		partView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openList("parts", INTENT_SPARE_PART);
			}
		});

		findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				tryCreateRequest();
			}
		});

		imageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				openGallery();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			String selectedItem = data.getStringExtra("result");
			if (requestCode == INTENT_AUTO_NAME)
			{
				nameView.setText(selectedItem);
			}
			else if (requestCode == INTENT_SPARE_PART)
			{
				partView.setText(selectedItem);
			}
		}
	}

	private void openList(String dbNodeName, int requestCode)
	{
		Intent intent = new Intent(this, ListActivity.class);
		intent.putExtra("dbNodeName", dbNodeName);
		startActivityForResult(intent, requestCode);
	}

	@Override
	protected int getFormLayoutId()
	{
		return R.layout.form_new_ticket;
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.activityNewTicket;
	}

	@Override
	protected void onRecivedImage(Uri imageUri)
	{
		this.imageUri = imageUri;
		Glide.with(this)
				.load(imageUri)
				.into(imageView);
	}

	@Override
	protected void populateWithTemplate(@NonNull Ticket template)
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
		partView.setImeOptions(IME_ACTION_DONE);
	}

	@Override
	protected void tryCreateRequest()
	{
		boolean valid = true;

		String name = nameView.getText().toString();
		String part = partView.getText().toString();

		int year;
		try {
			year = Integer.valueOf(yearView.getText().toString());
		} catch(NumberFormatException | NullPointerException e) {
			yearView.setError(getString(R.string.errorInvalidField));
			return;
		}

		if (name.isEmpty())
		{
			nameView.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}

		if (part.isEmpty())
		{
			partView.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}

		if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR))
		{
			yearView.setError(getString(R.string.errorInvalidField));
			valid = false;
		}

		if (imageUri == null)
		{
			Toast.makeText(this, getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			valid = false;
		}

		if (valid)
		{
			String vin = vinView.getText().toString();
			String engine = engineView.getText().toString();
			String comment = commentView.getText().toString();
			submit(new Ticket(uid, name, vin, year, engine, comment, part), imageUri);
		}
	}
}
