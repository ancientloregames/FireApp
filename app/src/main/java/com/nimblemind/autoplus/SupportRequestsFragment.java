package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public abstract class SupportRequestsFragment<MODEL extends Request> extends RequestsFragment<MODEL>
{

	public SupportRequestsFragment()
	{
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		getActivity().setTitle(getActivityTitle());
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == INTENT_SUPPORT_REQUEST_DETAILS)
			{
				assignRequest(data.getStringExtra("requestKey"));
			}
		}
	}

	private void assignRequest(String requestKey)
	{
		database.child(requestKey).child("sid").setValue(uid);
	}

	@StringRes
	protected abstract int getActivityTitle();

	@Override
	protected int getIntentCodeForRequestDetails()
	{
		return INTENT_SUPPORT_REQUEST_DETAILS;
	}
}
