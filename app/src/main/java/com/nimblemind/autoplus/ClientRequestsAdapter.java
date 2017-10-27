package com.nimblemind.autoplus;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.nimblemind.autoplus.swipereveallayout.ViewBinderHelper;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/26/2017.
 */

abstract class ClientRequestsAdapter<MODEL extends Request, HOLDER extends RequestViewHolder<MODEL>>
		extends RequestsAdapter<MODEL, HOLDER, ClientRequestsAdapter.Listener<MODEL>>
{
	interface Listener<MODEL extends Request> extends AdapterListener<MODEL>
	{
		void onCreateRequestClicked(MODEL request);
		void onDeleteRequestClicked(String key);
		void onCancelDeleteRequestClicked(String key);
	}

	final ViewBinderHelper binderHelper = new ViewBinderHelper();

	ClientRequestsAdapter(FirebaseRecyclerOptions<MODEL> options, Listener<MODEL> listener)
	{
		super(options, listener);
	}
}
