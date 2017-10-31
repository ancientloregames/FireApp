package com.nimblemind.autoplus;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.lang.ref.WeakReference;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/26/2017.
 */

abstract class RequestsAdapter<MODEL extends Request, HOLDER extends RequestViewHolder<MODEL>, LISTENER extends AdapterListener>
		extends FirebaseRecyclerAdapter<MODEL, HOLDER>
{
	WeakReference<LISTENER> listener;

	RequestsAdapter(FirebaseRecyclerOptions<MODEL> options, LISTENER listener)
	{
		super(options);
		this.listener = new WeakReference<>(listener);
	}

	@Override
	protected void onBindViewHolder(HOLDER holder, int position, MODEL model)
	{
		holder.bindToData(model);

		bindItem(holder, model);
	}

	@Override
	public HOLDER onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(getModelLayoutId(), parent, false);
		return createViewHolder(view);
	}

	protected abstract void bindItem(HOLDER holder, MODEL model);

	@LayoutRes
	protected abstract int getModelLayoutId();

	protected abstract HOLDER createViewHolder(View view);
}
