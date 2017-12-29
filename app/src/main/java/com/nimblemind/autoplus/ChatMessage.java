package com.nimblemind.autoplus;

import java.util.ArrayList;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/28/2017.
 */

public final class ChatMessage
{
	public final long timestamp;	// Set on server
	public final String senderId;
	public final String senderName;
	public final String text;
	public final ArrayList<String> images;

	public ChatMessage()
	{
		senderId = null;
		senderName = null;
		text = null;
		timestamp = 0;
		images = null;
	}

	public ChatMessage(String senderId, String senderName, String text, ArrayList<String> images)
	{
		this.senderId = senderId;
		this.senderName = senderName;
		this.text = text;
		this.timestamp = 0;
		this.images = images;
	}
}
