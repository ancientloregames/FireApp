package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/28/2017.
 */

public final class ChatMessage
{
	public final long timestamp;	// Set on server
	public final String senderId;
	public final String senderName;
	public final String text;

	public ChatMessage()
	{
		senderId = null;
		senderName = null;
		text = null;
		timestamp = 0;
	}

	public ChatMessage(String senderId, String senderName, String text)
	{
		this.senderId = senderId;
		this.senderName = senderName;
		this.text = text;
		this.timestamp = 0;
	}
}
