package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/22/2017.
 */

public abstract class ClientRequestsFragment<MODEL extends Request> extends RequestsFragment<MODEL>
{
	protected Set<String> deleteCandidates = new HashSet<>();

	public ClientRequestsFragment()
	{
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		view.findViewById(R.id.buttonNewRequest).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createNewRequest(null);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == INTENT_NEW_REQUEST)
			{
				MODEL newRequest = data.getParcelableExtra("request");
				ArrayList<TitledUri> images = data.getParcelableArrayListExtra("images");
				sendRequest(newRequest, images);
			}
			else if (requestCode == INTENT_REQUEST_DETAILS)
			{
				MODEL request = data.getParcelableExtra("request");
				if (request != null)
				{
					createNewRequest(request);
				}
			}
		}
	}

	protected void createNewRequest(@Nullable Request template)
	{
		Intent intent = new Intent(getActivity(), getNewRequestActivityClass());
		intent.putExtra("uid", uid);
		if (template != null)
		{
			intent.putExtra("template", template);
		}
		startActivityForResult(intent, INTENT_NEW_REQUEST);
	}

	@Override
	public void onDestroy()
	{
		for (String key : deleteCandidates)
		{
			deleteRequest(key);
		}
		super.onDestroy();
	}

	@CallSuper
	protected String sendRequest(MODEL request, ArrayList<TitledUri> images)
	{
		String requestId = database.push().getKey();
		database.child(requestId).setValue(request);
		return requestId;
	}

	protected void deleteRequest(final String key)
	{
		database.child(key).removeValue();
	}

	protected abstract Class<? extends NewRequestActivity> getNewRequestActivityClass();

	@StringRes
	protected abstract int getActivityTitle();
}
