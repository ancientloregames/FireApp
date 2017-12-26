package com.nimblemind.autoplus;

import android.content.res.Resources;
import android.view.View;
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

	private static String textUploading;
	private static String textPtsPhoto;
	private static String textPartPhoto;
	private static String textNoNewMessages;

	public ClientTicketViewHolder(View itemView)
	{
		super(itemView);

		Resources res = itemView.getContext().getResources();
		textUploading = res.getString(R.string.textUploading);
		textPtsPhoto = res.getString(R.string.textPhotoPTS);
		textPartPhoto = res.getString(R.string.textPhotoPart);
		textNoNewMessages = res.getString(R.string.textNoNewMessages);

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
		infoView.setText(ticket.timestamp > 0
				? Utils.getDate(ticket.timestamp, "dd MMMM, hh:mm")
				: textUploading);
		String sparePart = ticket.spareParts.get(0);
		sparePartView.setText(!sparePart.isEmpty()
				? sparePart
				: textPartPhoto);
		autoNameView.setText(ticket.autoBrand != null
				? String.format("%s %s, %d", ticket.autoBrand, ticket.autoModel, ticket.year)
				: textPtsPhoto);
		notificationView.setText(textNoNewMessages);
	}

	public SwipeRevealLayout getView()
	{
		return (SwipeRevealLayout) itemView;
	}
}
