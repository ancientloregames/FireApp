package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

public class ChatActivity extends AppCompatActivity
{
	protected static final int INTENT_ADD_IMAGE = 100;

	private FirebaseRecyclerAdapter adapter;

	private final ArrayList<TitledUri> images = new ArrayList<>();

	private final ArrayList<String> imagesNames = new ArrayList<>();

	private View addImageButton;

	private ImageView imageView;
	private TextView textField;

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
		final View sendButton = findViewById(R.id.buttonSend);
		final View deleteImageButton = findViewById(R.id.buttonDelete);

		textField = findViewById(R.id.textField);
		addImageButton = findViewById(R.id.buttonAddImage);
		imageView = findViewById(R.id.image);

		Intent intent = getIntent();
		final String uid = intent.getStringExtra("uid");
		final String userName = intent.getStringExtra("userName");
		final String requestId = intent.getStringExtra("requestId");
		final Request request = intent.getParcelableExtra("request");

		StorageReference requestStorageRef = FirebaseStorage.getInstance()
				.getReference("photos").child(request.uid).child(request.storageFolder);

		final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(requestId);

		SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
			@Override
			public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
				return dataSnapshot.getValue(ChatMessage.class);
			}
		};

		FirebaseRecyclerOptions<ChatMessage> options = new FirebaseRecyclerOptions.Builder<ChatMessage>()
				.setQuery(chatRef, parser)
				.build();

		adapter = new ChatAdapter(options, uid, requestStorageRef);

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

		addImageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(ChatActivity.this, GalleryActivity.class);
				startActivityForResult(intent, INTENT_ADD_IMAGE);
			}
		});

		deleteImageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				resetImageAddition();
			}
		});

		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String text = textField.getText().toString();
				if (!text.isEmpty() || !images.isEmpty())
				{
					sendMessage(new ChatMessage(uid, userName, text, imagesNames), chatRef);
					ChatActivity.this.startService(new Intent(ChatActivity.this, ImageUploadService.class)
							.putParcelableArrayListExtra(ImageUploadService.EXTRA_IMAGES, images)
							.putExtra(ImageUploadService.EXTRA_PATH, new String[] {request.uid, request.storageFolder})
							.setAction(ImageUploadService.ACTION_UPLOAD));
					clearMessageData();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == INTENT_ADD_IMAGE && resultCode == RESULT_OK)
		{
			Uri imageUri = data.getData();
			if (imageUri != null)
			{
				addImage(imageUri);
			}
		}
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

	private void sendMessage(ChatMessage message, DatabaseReference chatRef)
	{
		String key = chatRef.push().getKey();
		chatRef.child(key).setValue(message);
	}

	private void resetImageAddition()
	{
		((View)imageView.getParent()).setVisibility(View.GONE);
		imageView.setImageURI(null);
		addImageButton.setEnabled(true);
	}

	private void clearMessageData()
	{
		textField.setText("");
		resetImageAddition();
		imagesNames.clear();
		images.clear();
	}

	private void addImage(Uri uri)
	{
		addImageButton.setEnabled(false);
		String name = String.valueOf(System.currentTimeMillis());
		images.add(new TitledUri(name, uri));
		imagesNames.add(name);
		imageView.setImageURI(uri);
		((View)imageView.getParent()).setVisibility(View.VISIBLE);
	}
}