package com.nimblemind.autoplus;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/15/2017.
 */

public abstract class RequestViewHolder<MODEL extends Request> extends RecyclerView.ViewHolder
{
	public RequestViewHolder(View itemView)
	{
		super(itemView);
	}

	protected abstract void bindToData(MODEL request);
}
