package com.nimblemind.autoplus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public abstract class RequestListFragment<MODEL extends Request, VIEWHOLDER extends RequestViewHolder> extends Fragment
{
	private Listener listener;

	private String uid;

	private DatabaseReference database;

	private FirebaseRecyclerAdapter<MODEL, VIEWHOLDER> adapter;

	public RequestListFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(getFragmentLayoutId(), container, false);

		database = FirebaseDatabase.getInstance().getReference("requests");

		rootView.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				listener.onLogout();
			}
		});

		return rootView;
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if (context instanceof Listener)
		{
			listener = (Listener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
					+ " must implement " + this.getClass().getSimpleName() + ".Listener");
		}

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

		RecyclerView recycler = getView().findViewById(R.id.recycler);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

		adapter = new FirebaseRecyclerAdapter<MODEL, VIEWHOLDER>
				(getModelClass(), getModelLayoutId(), getViewHolderClass(), getQuery(database))
		{
			@Override
			protected void populateViewHolder(VIEWHOLDER viewHolder, MODEL model, int position)
			{
				viewHolder.bindToData(model);

				setButtonListeners(viewHolder, model);
			}
		};

		recycler.setAdapter(adapter);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (adapter != null)
		{
			adapter.cleanup();
		}
	}

	protected String getUid()
	{
		return uid;
	}

	@LayoutRes
	protected abstract int getFragmentLayoutId();

	@LayoutRes
	protected abstract int getModelLayoutId();

	protected abstract Class<MODEL> getModelClass();

	protected abstract Class<VIEWHOLDER> getViewHolderClass();

	protected abstract Query getQuery(DatabaseReference databaseReference);

	protected abstract void setButtonListeners(VIEWHOLDER viewHolder, MODEL model);

	interface Listener
	{
		void onLogout();
	}
}
