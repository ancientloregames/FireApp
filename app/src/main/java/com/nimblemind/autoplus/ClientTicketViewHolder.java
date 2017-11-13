package com.nimblemind.autoplus;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.nimblemind.autoplus.swipereveallayout.SwipeRevealLayout;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/12/2017.
 */

public class ClientTicketViewHolder extends RequestViewHolder<Ticket>
{
	TextView infoView;
	TextView sparePartView;
	TextView autoNameView;
	TextView notificationView;
	View newRequestButton;
	View cancelDeletionButton;
	View itemLayout;

	public ClientTicketViewHolder(View itemView)
	{
		super(itemView);

		itemLayout = itemView.findViewById(R.id.itemLayout);
		infoView = itemView.findViewById(R.id.infoView);
		sparePartView = itemView.findViewById(R.id.sparePartView);
		autoNameView = itemView.findViewById(R.id.autoNameView);
		notificationView = itemView.findViewById(R.id.notificationView);
		newRequestButton = itemView.findViewById(R.id.newRequestButton);
		cancelDeletionButton = itemView.findViewById(R.id.buttonCancelDeletion);
	}

	public void bindToData(Ticket ticket)
	{
		infoView.setText(Utils.getDate(ticket.timestamp, "dd MMMM, hh:mm"));
		sparePartView.setText(ticket.partType);
		autoNameView.setText(ticket.autoName);
		notificationView.setText(itemView.getResources().getString(R.string.textNoNewMessages));
	}

	public SwipeRevealLayout getView()
	{
		return (SwipeRevealLayout) itemView;
	}
}
