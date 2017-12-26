package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

public class ChatActivity extends AppCompatActivity
{
	FirebaseRecyclerAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityChat);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final RecyclerView recycler = findViewById(R.id.recycler);
		final TextView textField = findViewById(R.id.textField);
		final Button sendButton = findViewById(R.id.buttonSend);

		Intent intent = getIntent();
		final String uid = intent.getStringExtra("uid");
		final String userName = intent.getStringExtra("userName");
		final String requestKey = intent.getStringExtra("requestKey");

		final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(requestKey);

		SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
			@Override
			public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
				return dataSnapshot.getValue(ChatMessage.class);
			}
		};

		FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
				.setQuery(chatRef, parser)
				.build();

		adapter = new ChatAdapter(options, uid);

		final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setStackFromEnd(true);

		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
		{
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount)
			{
				super.onItemRangeInserted(positionStart, itemCount);
				int friendlyMessageCount = adapter.getItemCount();
				int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
				if (lastVisiblePosition == -1 ||
						(positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1)))
				{
					recycler.scrollToPosition(positionStart);
				}
			}
		});

		recycler.setLayoutManager(layoutManager);
		recycler.setAdapter(adapter);

		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String text = textField.getText().toString();
				if (!text.isEmpty())
				{
					sendMessage(new ChatMessage(uid, userName, text), chatRef);
					textField.setText("");
				}
			}
		});
	}

	@Override
	public void onResume()
	{
		super.onResume();
		adapter.startListening();
	}

	@Override
	public void onPause()
	{
		adapter.stopListening();
		super.onPause();
	}

	private void sendMessage(ChatMessage message, DatabaseReference chatRef)
	{
		String key = chatRef.push().getKey();
		chatRef.child(key).setValue(message);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}