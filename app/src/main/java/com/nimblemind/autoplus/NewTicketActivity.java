package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/22/2017.
 */

public class NewTicketActivity extends NewRequestActivity
{
	@Override
	protected int getActivityTitle()
	{
		return R.string.activityNewTicket;
	}

	@Override
	protected TitledFragment[] getFragments()
	{
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra("uid"))
		{
			Bundle args = new Bundle();
			args.putString("uid", intent.getStringExtra("uid"));
			if (intent.hasExtra("template"))
			{
				args.putParcelable("template", intent.getParcelableExtra("template"));
			}

			Fragment textFragment = new NewTicketTextFragment();
			textFragment.setArguments(args);

			return new TitledFragment[] {
					new TitledFragment(getString(R.string.textNewRequestTextMode), textFragment)
			};
		}
		else throw new RuntimeException("The Uid must be passed in order to create new ticket");
	}
}
