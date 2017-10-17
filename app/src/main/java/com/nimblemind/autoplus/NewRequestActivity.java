package com.nimblemind.autoplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;


public class NewRequestActivity extends AppCompatActivity
{
	private TextView nameView;
	private TextView vinView;
	private TextView yearView;
	private TextView engineView;
	private TextView partView;
	private TextView commentView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_request);

		final String uid;
		Intent intent = getIntent();
		if (intent != null)
			uid = intent.getStringExtra("uid");
		else throw new RuntimeException("The Uid must be passed in order to create new ticket");

		nameView = findViewById(R.id.textName);
		vinView = findViewById(R.id.textVin);
		yearView = findViewById(R.id.textYear);
		engineView = findViewById(R.id.textEngine);
		partView = findViewById(R.id.textPart);
		commentView = findViewById(R.id.textComment);

		findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				String name = nameView.getText().toString();
				String part = partView.getText().toString();

				int vin;
				int year;
				try {
					vin = Integer.valueOf(vinView.getText().toString());
				} catch(NumberFormatException | NullPointerException e) {
					vinView.setError(getString(R.string.errorInvalidField));
					return;
				}
				try {
					year = Integer.valueOf(yearView.getText().toString());
				} catch(NumberFormatException | NullPointerException e) {
					yearView.setError(getString(R.string.errorInvalidField));
					return;
				}

				if (validate(name, part, year))
				{

					String engine = engineView.getText().toString();
					String comment = commentView.getText().toString();

					Ticket ticket = new Ticket(uid, name, vin, year, engine, comment, part);
					Intent resultIntent = new Intent();
					resultIntent.putExtra("request", ticket);
					setResult(RESULT_OK, resultIntent);
					finish();
				}
			}
		});
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

		return result;
	}
}
