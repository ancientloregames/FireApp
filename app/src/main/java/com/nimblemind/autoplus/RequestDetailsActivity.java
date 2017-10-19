package com.nimblemind.autoplus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class RequestDetailsActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_details);

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
}
