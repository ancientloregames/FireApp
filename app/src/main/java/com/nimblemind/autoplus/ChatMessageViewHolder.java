package com.nimblemind.autoplus;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/28/2017.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder
{
	TextView infoView;
	TextView messageView;

	public ChatMessageViewHolder(View itemView)
	{
		super(itemView);

		infoView = itemView.findViewById(R.id.textInfo);
		messageView = itemView.findViewById(R.id.textMessage);
	}

	public void bindToData(ChatMessage message)
	{
		infoView.setText(Utils.getDate(message.timestamp, "hh:mm"));
		messageView.setText(message.text);
	}
}
