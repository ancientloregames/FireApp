package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public abstract class RequestListFragment<MODEL extends Request, VIEWHOLDER extends RequestViewHolder> extends Fragment
{
	public static final int INTENT_NEW_REQUEST = 101;
	public static final int INTENT_REQUEST_DETAILS = 102;

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

		rootView.findViewById(R.id.newRequestButton).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createNewRequest(null);
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
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setReverseLayout(true);
		recycler.setLayoutManager(layoutManager);

		adapter = new FirebaseRecyclerAdapter<MODEL, VIEWHOLDER>
				(getModelClass(), getModelLayoutId(), getViewHolderClass(), getQuery(database))
		{
			@Override
			protected void populateViewHolder(VIEWHOLDER viewHolder, MODEL model, int position)
			{
				viewHolder.bindToData(model);

				bindItem(viewHolder, model);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == INTENT_NEW_REQUEST)
			{
				Request newRequest = (Request) data.getSerializableExtra("request");
				Uri photoUri = data.getParcelableExtra("photo");
				sendRequest(newRequest, photoUri);
			}
		}
	}

	private void sendRequest(Request request, Uri photoUri)
	{
		String key = database.push().getKey();
		database.child(key).setValue(request);

		getActivity().startService(new Intent(getActivity(), ImageUploadService.class)
				.putExtra(ImageUploadService.EXTRA_FILE_URI, photoUri)
				.putExtra(ImageUploadService.EXTRA_FILE_NAME, key)
				.putExtra(ImageUploadService.EXTRA_FILE_FOLDER, getUid())
				.setAction(ImageUploadService.ACTION_UPLOAD));
	}

	protected void deleteRequest(final int position)
	{
		adapter.getRef(position).removeValue();
	}

	protected void createNewRequest(@Nullable Request template)
	{
		Intent intent = new Intent(getActivity(), NewRequestActivity.class);
		intent.putExtra("uid", getUid());
		intent.putExtra("template", template);
		startActivityForResult(intent, INTENT_NEW_REQUEST);
	}

	protected void showRequestDetails(@NonNull Request request)
	{
		Intent intent = new Intent(getActivity(), RequestDetailsActivity.class);
		intent.putExtra("request", request);
		startActivityForResult(intent, INTENT_REQUEST_DETAILS);
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

	protected abstract void bindItem(VIEWHOLDER viewHolder, MODEL model);

	interface Listener
	{
		void onLogout();
	}
}
