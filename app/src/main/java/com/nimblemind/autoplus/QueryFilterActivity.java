package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 12/27/2017.
 */

public class QueryFilterActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_filter);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(R.string.activityQueryFilter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		findViewById(R.id.radioButtonOpened).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectFilter(Constants.QUERY_MODE_OPENED);
			}
		});

		findViewById(R.id.radioButtonOwn).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectFilter(Constants.QUERY_MODE_OWN);
			}
		});
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

	private void selectFilter(int filterId)
	{
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_QUERY_FILTER_CODE, filterId);
		setResult(RESULT_OK, intent);
		finish();
	}
}
