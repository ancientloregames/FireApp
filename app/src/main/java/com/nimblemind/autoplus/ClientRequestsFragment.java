package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
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
				switch (data.getAction())
				{
					case DetailRequestActivity.ACTION_OPEN_CHAT:
						String requestId = data.getStringExtra("requestId");
						openChat(requestId, request);
						break;
					case DetailRequestActivity.ACTION_NEW_REQUEST:
						createNewRequest(request);
						break;
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

	@Override
	protected void resetUnreadMessages(String requestId)
	{
		database.child(requestId).child("unreadSupMsgs").setValue(0);
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
}
