package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public abstract class RequestsFragment<MODEL extends Request> extends Fragment
{
	public static final int INTENT_NEW_REQUEST = 101;
	public static final int INTENT_REQUEST_DETAILS = 102;
	public static final int INTENT_CHAT = 110;

	protected String uid;
	protected String userName;

	protected DatabaseReference database;

	protected RequestsAdapter adapter;
	protected RecyclerView recycler;

	protected View progressBar;

	public RequestsFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(getFragmentLayoutId(), container, false);

		database = FirebaseDatabase.getInstance().getReference(Constants.DB_REF_REQUESTS);

		progressBar = rootView.findViewById(R.id.progressBar);

		recycler = rootView.findViewById(R.id.recycler);

		return rootView;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		getActivity().setTitle(getActivityTitle());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		Bundle arguments = getArguments();

		if (arguments != null)
		{
			uid = arguments.getString(Constants.EXTRA_UID);
			userName = arguments.getString(Constants.EXTRA_USER_NAME);
		}
		else
		{
			throw new RuntimeException("Uid was not passed to the list fragment!");
		}

		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, true);
		layoutManager.setStackFromEnd(true);
		recycler.setLayoutManager(layoutManager);

		adapter = createAdapter(getModelClass(), getInitialQuery(database));

		recycler.setAdapter(adapter);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		adapter.startListening();
	}

	@Override
	public void onStop()
	{
		progressBar.setVisibility(View.VISIBLE);
		adapter.stopListening();
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == INTENT_CHAT && data != null)
		{
			resetUnreadMessages(data.getStringExtra(Constants.EXTRA_REQUEST_ID));
		}
	}



	protected RequestsAdapter createAdapter(final Class<MODEL> modelClass, Query query)
	{
		SnapshotParser<MODEL> parser = new SnapshotParser<MODEL>() {
			@Override
			public MODEL parseSnapshot(DataSnapshot dataSnapshot) {
				return dataSnapshot.getValue(modelClass);
			}
		};

		FirebaseRecyclerOptions<MODEL> options = new FirebaseRecyclerOptions.Builder<MODEL>()
				.setQuery(query, parser)
				.build();

		adapter = createAdapter(options);

		return adapter;
	}

	protected void showRequestDetails(@NonNull Request request, String requestId)
	{
		Intent intent = new Intent(getActivity(), getDetailRequestActivityClass());
		intent.putExtra(Constants.EXTRA_UID, uid);
		intent.putExtra(Constants.EXTRA_REQUEST_ID, requestId);
		intent.putExtra(Constants.EXTRA_REQUEST, request);
		startActivityForResult(intent, INTENT_REQUEST_DETAILS);
	}

	protected void openChat(String requestId, Request request)
	{
		resetUnreadMessages(requestId);
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(Constants.EXTRA_UID, uid);
		intent.putExtra(Constants.EXTRA_USER_NAME, userName);
		intent.putExtra(Constants.EXTRA_REQUEST_ID, requestId);
		intent.putExtra(Constants.EXTRA_REQUEST, request);
		startActivityForResult(intent, INTENT_CHAT);
	}

	protected abstract void resetUnreadMessages(String requestId);

	@LayoutRes
	protected abstract int getFragmentLayoutId();

	protected abstract Class<MODEL> getModelClass();

	protected abstract Query getInitialQuery(DatabaseReference databaseReference);

	protected abstract RequestsAdapter createAdapter(FirebaseRecyclerOptions<MODEL> options);

	protected abstract Class<? extends DetailRequestActivity> getDetailRequestActivityClass();

	@StringRes
	protected abstract int getActivityTitle();
}
