package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

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
	public void onAttach(Context context)
	{
		super.onAttach(context);

		getActivity().setTitle(getActivityTitle());
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
				sendRequest(newRequest);
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
	protected String sendRequest(MODEL request)
	{
		String key = database.push().getKey();
		database.child(key).setValue(request);
		return key;
	}

	protected void deleteRequest(final String key)
	{
		database.child(key).removeValue();
	}

	protected abstract Class<? extends NewRequestActivity> getNewRequestActivityClass();

	@StringRes
	protected abstract int getActivityTitle();
}
