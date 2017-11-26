package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;


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
		Bundle args = new Bundle();
		args.putString("uid", uid);
		if (mode != Mode.DEFAULT)
		{
			args.putParcelable("template", intent.getParcelableExtra("template"));
		}

		ArrayList<TitledFragment> list = new ArrayList<>();
		if (mode == Mode.DEFAULT || mode == Mode.TEXT_BASED)
		{
			Fragment textFragment = new NewTicketTextFragment();
			textFragment.setArguments(args);
			list.add(new TitledFragment(getString(R.string.textNewRequestTextMode), textFragment));
		}
		if (mode == Mode.DEFAULT || mode == Mode.PHOTO_BASED)
		{
			Fragment photoFragment = new NewTicketPhotoFragment();
			photoFragment.setArguments(args);
			list.add(new TitledFragment(getString(R.string.textNewRequestPhotoMode), photoFragment));
		}

		return list.toArray(new TitledFragment[list.size()]);
	}
}
