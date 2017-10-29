package com.nimblemind.autoplus;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

public class SupportTicketActivity extends SupportRequestActivity<Ticket>
{
	private TextView infoView;
	private TextView uidView;
	private TextView partView;
	private TextView nameView;
	private TextView yearView;
	private TextView vinView;
	private TextView commentView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		infoView = findViewById(R.id.textRequestInfo);
		uidView = findViewById(R.id.textUid);
		partView = findViewById(R.id.textAutoPart);
		nameView = findViewById(R.id.textAutoName);
		yearView = findViewById(R.id.textAutoYear);
		vinView = findViewById(R.id.textAutoVin);
		commentView = findViewById(R.id.textComment);
	}

	@Override
	protected void populateForm(Ticket request)
	{
		infoView.setText(getString(R.string.textRequestInfo, request.id, Utils.getDate(request.timestamp)));
		uidView.setText(request.uid);
		partView.setText(request.partType);
		nameView.setText(request.autoName);
		yearView.setText(String.valueOf(request.year));
		vinView.setText(String.valueOf(request.vin));
		commentView.setText(request.comment);

		if (!request.sid.isEmpty())
		{
			findViewById(R.id.buttonTakeRequest).setVisibility(View.GONE);
			findViewById(R.id.buttonOpenChat).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openChat();
				}
			});
		}
		else
		{
			findViewById(R.id.buttonOpenChat).setVisibility(View.GONE);
			findViewById(R.id.buttonTakeRequest).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					takeRequest();
				}
			});
		}
	}

	@Override
	protected int getFormLayoutId()
	{
		return R.layout.support_ticket_form;
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.activitySupportTicket;
	}
}
