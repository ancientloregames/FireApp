package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

	protected String uid;

	protected DatabaseReference database;

	protected RequestsAdapter adapter;

	public RequestsFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(getFragmentLayoutId(), container, false);

		database = FirebaseDatabase.getInstance().getReference("requests");

		return rootView;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		getActivity().setTitle(R.string.fragmentClientRequestListName);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		Bundle arguments = getArguments();

		if (arguments != null)
			uid = arguments.getString("uid");
		else
		{
			throw new RuntimeException("Uid was not passed to the list fragment!");
		}

		final RecyclerView recycler = getView().findViewById(R.id.recycler);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setReverseLayout(true);
		recycler.setLayoutManager(layoutManager);

		SnapshotParser<MODEL> parser = new SnapshotParser<MODEL>() {
			@Override
			public MODEL parseSnapshot(DataSnapshot dataSnapshot) {
				return dataSnapshot.getValue(getModelClass());
			}
		};

		FirebaseRecyclerOptions<MODEL> options = new FirebaseRecyclerOptions.Builder<MODEL>()
				.setQuery(getQuery(database), parser)
				.build();

		adapter = createAdapter(options);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
		{
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount)
			{
				super.onItemRangeInserted(positionStart, itemCount);
				recycler.scrollToPosition(positionStart);
			}
		});

		recycler.setAdapter(adapter);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		adapter.startListening();
	}

	@Override
	public void onPause()
	{
		adapter.stopListening();
		super.onPause();
	}

	protected void showRequestDetails(@NonNull Request request)
	{
		Intent intent = new Intent(getActivity(), getDetailsRequestActivityClass());
		intent.putExtra("request", request);
		startActivityForResult(intent, INTENT_REQUEST_DETAILS);
	}

	@LayoutRes
	protected abstract int getFragmentLayoutId();

	protected abstract Class<MODEL> getModelClass();

	protected abstract Query getQuery(DatabaseReference databaseReference);

	protected abstract RequestsAdapter createAdapter(FirebaseRecyclerOptions<MODEL> options);

	protected abstract Class<? extends AppCompatActivity> getDetailsRequestActivityClass();

	@StringRes
	protected abstract int getActivityTitle();
}
