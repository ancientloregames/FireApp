package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

public class ChatActivity extends AppCompatActivity
{
	private RecyclerView recycler;
	private TextView textField;
	private Button sendButton;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityChat);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		recycler = findViewById(R.id.recycler);
		textField = findViewById(R.id.textField);
		sendButton = findViewById(R.id.buttonSend);
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