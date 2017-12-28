package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public abstract class SupportRequestsFragment<MODEL extends Request> extends RequestsFragment<MODEL>
{
	public final static int QUERY_MODE_OPENED = 1;
	public final static int QUERY_MODE_OWN = 2;

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
				String requestId = data.getStringExtra("requestKey");
				assignRequest(requestId);
				openChat(requestId);
			}
		}
	}

	@Override
	protected Query getInitialQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild("sid").equalTo("");
	}

	protected Query getFilterQuery(DatabaseReference databaseReference, int filterCode)
	{
		switch (filterCode)
		{
			default:
			case QUERY_MODE_OPENED:
				return databaseReference.orderByChild("sid").equalTo("");
			case QUERY_MODE_OWN:
				return databaseReference.orderByChild("sid").equalTo(uid);
		}
	}

	public void applyFilter(int filterCode)
	{
		adapter = createAdapter(getModelClass(), getFilterQuery(database, filterCode));

		recycler.setAdapter(adapter);
	}

	protected void assignRequest(String requestKey)
	{
		database.child(requestKey).child("sid").setValue(uid);
	}
}
