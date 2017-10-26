package com.nimblemind.autoplus;

import android.os.Bundle;
import android.widget.TextView;


public class ClientTicketActivity extends ClientRequestActivity<Ticket>
{
	private TextView infoView;
	private TextView partView;
	private TextView nameView;
	private TextView yearView;
	private TextView vinView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		infoView = findViewById(R.id.textRequestInfo);
		partView = findViewById(R.id.textAutoPart);
		nameView = findViewById(R.id.textAutoName);
		yearView = findViewById(R.id.textAutoYear);
		vinView = findViewById(R.id.textAutoVin);
	}

	@Override
	protected void populateForm(Ticket request)
	{
		infoView.setText(getString(R.string.textRequestInfo, request.id, Utils.getDate(request.timestamp)));
		partView.setText(request.partType);
		nameView.setText(request.autoName);
		yearView.setText(String.valueOf(request.year));
		vinView.setText(String.valueOf(request.vin));
	}

	@Override
	protected int getFormLayoutId()
	{
		return R.layout.form_detail_ticket;
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.activityDetailTicket;
	}
}
