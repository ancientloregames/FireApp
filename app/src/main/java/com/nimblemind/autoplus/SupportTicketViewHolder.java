package com.nimblemind.autoplus;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public class SupportTicketViewHolder extends RequestViewHolder<Ticket>
{
	TextView infoView;
	TextView sparePartView;
	TextView autoNameView;
	TextView notificationView;
	Button answerRequestButton;

	private static String textUploading;
	private static String textPtsPhoto;
	private static String textPartPhoto;
	private static String textNoNewMessages;

	public SupportTicketViewHolder(View itemView)
	{
		super(itemView);

		Resources res = itemView.getContext().getResources();
		textUploading = res.getString(R.string.textUploading);
		textPtsPhoto = res.getString(R.string.textPhotoPTS);
		textPartPhoto = res.getString(R.string.textPhotoPart);
		textNoNewMessages = res.getString(R.string.textNoNewMessages);

		infoView = itemView.findViewById(R.id.infoView);
		sparePartView = itemView.findViewById(R.id.sparePartView);
		autoNameView = itemView.findViewById(R.id.autoNameView);
		notificationView = itemView.findViewById(R.id.notificationView);
		answerRequestButton = itemView.findViewById(R.id.buttonAnswerRequest);
	}

	@Override
	protected void bindToData(Ticket ticket)
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
}
