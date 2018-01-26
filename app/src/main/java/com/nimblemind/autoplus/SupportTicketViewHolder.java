package com.nimblemind.autoplus;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
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

	private static String textUploading;
	private static String textPtsPhoto;
	private static String textPartPhoto;
	private static String textNoMessages;

	public SupportTicketViewHolder(View itemView)
	{
		super(itemView);

		Resources res = itemView.getContext().getResources();
		textUploading = res.getString(R.string.textUploading);
		textPtsPhoto = res.getString(R.string.textPhotoPTS);
		textPartPhoto = res.getString(R.string.textPhotoPart);
		textNoMessages = res.getString(R.string.textNoMessages);

		infoView = itemView.findViewById(R.id.infoView);
		sparePartView = itemView.findViewById(R.id.sparePartView);
		autoNameView = itemView.findViewById(R.id.autoNameView);
		notificationView = itemView.findViewById(R.id.notificationView);
	}

	@Override
	protected void bindToData(Ticket ticket)
	{
		infoView.setText(ticket.timestamp > 0
				? Utils.getDate(ticket.timestamp, "dd MMMM, HH:mm")
				: textUploading);
		String sparePart = ticket.spareParts.get(0);
		sparePartView.setText(!sparePart.isEmpty()
				? sparePart
				: textPartPhoto);
		autoNameView.setText(ticket.autoBrand != null
				? String.format("%s %s, %d", ticket.autoBrand, ticket.autoModel, ticket.year)
				: textPtsPhoto);

		Resources resources = itemView.getContext().getResources();
		Drawable notificationImage;
		String notificationText;
		if (ticket.totalMsgs != 0)
		{
			if (ticket.unreadUsrMsgs != 0)
			{
				notificationImage = resources.getDrawable(R.drawable.asset_message_new);
				notificationText = resources.getString(R.string.textNewMessagesCount, ticket.unreadUsrMsgs);
			}
			else
			{
				notificationImage = resources.getDrawable(R.drawable.asset_message_has);
				notificationText = resources.getString(R.string.textMessagesCount, ticket.totalMsgs);
			}
		}
		else
		{
			notificationImage = resources.getDrawable(R.drawable.asset_message_empty);
			notificationText = textNoMessages;
		}
		int size = (int) notificationView.getTextSize();
		notificationImage.setBounds(0, 0 , size , size);
		notificationView.setCompoundDrawables(notificationImage, null, null, null);
		notificationView.setText(notificationText);
	}
}
