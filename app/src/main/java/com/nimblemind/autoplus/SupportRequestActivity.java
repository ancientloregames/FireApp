package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

public abstract class SupportRequestActivity<MODEL extends Request> extends AppCompatActivity
{
	protected String requestKey;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_request);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(getActivityTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		ViewGroup formContainer = findViewById(R.id.form);
		LayoutInflater.from(this).inflate(getFormLayoutId(), formContainer);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		if (savedInstanceState == null)
		{
			onFirstCreate();
		}
	}

	private void onFirstCreate()
	{
		Intent intent = getIntent();
		if (intent != null)
		{
			requestKey = intent.getStringExtra("requestKey");

			MODEL request = (MODEL) intent.getSerializableExtra("request");

			if (request != null)
			{
				populateForm(request);
			}
			else throw new RuntimeException("Existing request must be passed as an Extra");
		}
		else throw new RuntimeException("Existing request must be passed as an Extra");
	}

	protected void answerRequest()
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra("requestKey", requestKey);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	protected abstract void populateForm(MODEL request);

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

	@LayoutRes
	protected abstract int getFormLayoutId();

	@StringRes
	protected abstract int getActivityTitle();
}
