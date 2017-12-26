package com.nimblemind.autoplus;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/28/2017.
 */

public class ChatAdapter extends FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>
{
	private static final int VIEW_TYPE_USER = 1;
	private static final int VIEW_TYPE_COLLOCATOR = 2;

	private final String uid;

	public ChatAdapter(FirebaseRecyclerOptions<ChatMessage> options, @NonNull String uid)
	{
		super(options);
		this.uid = uid;
	}

	@Override
	protected void onBindViewHolder(ChatMessageViewHolder holder, int position, ChatMessage model)
	{
		holder.bindToData(model);
	}

	@Override
	public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		int layoutId = R.layout.chat_user_message;
		switch (viewType)
		{
			case VIEW_TYPE_USER:
				layoutId = R.layout.chat_user_message;
				break;
			case VIEW_TYPE_COLLOCATOR:
				layoutId = R.layout.chat_collocator_message;
				break;
		}
		return new ChatMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
	}

	@Override
	public int getItemViewType(int position)
	{
		return uid.equals(getItem(position).senderId)
				? VIEW_TYPE_USER
				: VIEW_TYPE_COLLOCATOR;
	}
}
