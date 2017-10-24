package com.nimblemind.autoplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;


public class RequestDetailsActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_details);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityRequestDetails);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView infoView = findViewById(R.id.textRequestInfo);
		TextView partView = findViewById(R.id.textAutoPart);
		TextView nameView = findViewById(R.id.textAutoName);
		TextView yearView = findViewById(R.id.textAutoYear);
		TextView vinView = findViewById(R.id.textAutoVin);

		Intent intent = getIntent();
		if (intent != null)
		{
			Ticket request = (Ticket) intent.getSerializableExtra("request");

			if (request != null)
			{
				infoView.setText(getString(R.string.textRequestInfo, request.id, Utils.getDate(request.timestamp)));
				partView.setText(request.partType);
				nameView.setText(request.autoName);
				yearView.setText(String.valueOf(request.year));
				vinView.setText(String.valueOf(request.vin));
			}
			else throw new RuntimeException("Existing request must be passed as an Extra");
		}
		else throw new RuntimeException("Existing request must be passed as an Extra");
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
}
