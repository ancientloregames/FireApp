package com.nimblemind.autoplus;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public class ClientTicketViewHolder extends RequestViewHolder<Ticket>
{
	TextView infoView;
	TextView sparePartView;
	TextView autoNameView;
	TextView notificationView;
	Button newRequestButton;
	ImageButton deleteRequestButton;

	public ClientTicketViewHolder(View itemView)
	{
		super(itemView);

		infoView = itemView.findViewById(R.id.infoView);
		sparePartView = itemView.findViewById(R.id.sparePartView);
		autoNameView = itemView.findViewById(R.id.autoNameView);
		notificationView = itemView.findViewById(R.id.notificationView);
		newRequestButton = itemView.findViewById(R.id.newRequestButton);
		deleteRequestButton = itemView.findViewById(R.id.deleteRequestButton);
	}

	public void bindToData(Ticket ticket)
	{
		infoView.setText(itemView.getResources()
				.getString(R.string.textRequestInfo, ticket.id, Utils.getDate(ticket.timestamp)));
		sparePartView.setText(ticket.partType);
		autoNameView.setText(ticket.autoName);
		notificationView.setText(itemView.getResources().getString(R.string.textNoNewMessages));
	}
}
