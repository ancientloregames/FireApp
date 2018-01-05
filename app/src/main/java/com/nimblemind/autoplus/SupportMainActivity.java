package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 12/10/2017.
 */

public class SupportMainActivity extends MainActivity<SupportRequestsFragment>
{
	public final static int INTENT_QUERY_FILTER = 90;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Toolbar toolbar = findViewById(R.id.toolbar);
		View filterButtonView = getLayoutInflater().inflate(R.layout.toolbar_filter_button, toolbar, false);
		filterButtonView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openQueryFilterActivity();
			}
		});
		toolbar.addView(filterButtonView);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == INTENT_QUERY_FILTER && resultCode == RESULT_OK)
		{
			int filterCode = data.getIntExtra(Constants.EXTRA_QUERY_FILTER_CODE, 0);
			fragment.applyFilter(filterCode);
		}
	}

	private void openQueryFilterActivity()
	{
		Intent intent = new Intent(this, QueryFilterActivity.class);
		startActivityForResult(intent, INTENT_QUERY_FILTER);
	}

	@Override
	protected SupportTicketsFragment getTicketsFragment()
	{
		return new SupportTicketsFragment();
	}
}