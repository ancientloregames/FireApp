package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static android.app.Activity.RESULT_OK;
import static com.nimblemind.autoplus.Constants.QUERY_MODE_OPENED;
import static com.nimblemind.autoplus.Constants.QUERY_MODE_OWN;
import static com.nimblemind.autoplus.Constants.QUERY_MODE_UNANSWERED;
import static com.nimblemind.autoplus.SupportDetailRequestActivity.ACTION_OPEN_CHAT;
import static com.nimblemind.autoplus.SupportDetailRequestActivity.ACTION_TAKE_REQUEST;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public abstract class SupportRequestsFragment<MODEL extends Request> extends RequestsFragment<MODEL>
{
	public SupportRequestsFragment()
	{
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
			if (requestCode == INTENT_REQUEST_DETAILS)
			{
				String requestId = data.getStringExtra(Constants.EXTRA_REQUEST_ID);
				MODEL request = data.getParcelableExtra(Constants.EXTRA_REQUEST);

				String action = data.getAction();
				if (ACTION_OPEN_CHAT.equals(action))
				{
					openChat(requestId, request);
				}
				else if (ACTION_TAKE_REQUEST.equals(action))
				{
					assignRequest(requestId);
					openChat(requestId, request);
				}
			}
		}
	}

	@Override
	protected Query getInitialQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild(Constants.DB_REF_SUPPORT_ID).equalTo("");
	}

	protected Query getFilterQuery(DatabaseReference databaseReference, int filterCode)
	{
		switch (filterCode)
		{
			default:
			case QUERY_MODE_OPENED:
				getActivity().setTitle(getString(R.string.fragmentSupportTicketList));
				return databaseReference.orderByChild(Constants.DB_REF_SUPPORT_ID).equalTo("");
			case QUERY_MODE_OWN:
			case QUERY_MODE_UNANSWERED:
				getActivity().setTitle(getString(R.string.fragmentSupportTicketsInProcessList));
				return databaseReference.orderByChild(Constants.DB_REF_SUPPORT_ID).equalTo(uid);
		}
	}

	public void applyFilter(int filterCode)
	{
		adapter = createAdapter(getModelClass(), getFilterQuery(database, filterCode));

		adapter.setFilterMode(filterCode);

		recycler.setAdapter(adapter);
	}

	@Override
	protected void resetUnreadMessages(String requestId)
	{
		database.child(requestId).child(Constants.DB_REF_UNREAD_CLIENT_MESSAGES).setValue(0);
	}

	protected void assignRequest(String requestKey)
	{
		database.child(requestKey).child(Constants.DB_REF_SUPPORT_ID).setValue(uid);
	}
}
