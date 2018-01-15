package com.nimblemind.autoplus;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

abstract class SupportRequestsAdapter<MODEL extends Request, HOLDER extends RequestViewHolder<MODEL>>
		extends RequestsAdapter<MODEL, HOLDER, SupportRequestsAdapter.Listener<MODEL>>
{
	interface Listener<MODEL extends Request> extends AdapterListener<MODEL>
	{
	}

	SupportRequestsAdapter(FirebaseRecyclerOptions<MODEL> options, Listener<MODEL> listener)
	{
		super(options, listener);
	}
}
