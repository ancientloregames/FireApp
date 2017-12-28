package com.nimblemind.autoplus;

import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public class SupportTicketsFragment extends SupportRequestsFragment<Ticket> implements SupportRequestsAdapter.Listener<Ticket>
{
	public SupportTicketsFragment()
	{
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.fragmentSupportTicketList;
	}

	@Override
	protected int getFragmentLayoutId()
	{
		return R.layout.support_list_fragment;
	}

	@Override
	protected Class<Ticket> getModelClass()
	{
		return Ticket.class;
	}

	@Override
	protected RequestsAdapter createAdapter(FirebaseRecyclerOptions<Ticket> options)
	{
		return new SupportTicketsAdapter(options, this){
			@Override
			public void onDataChanged()
			{
				progressBar.setVisibility(View.GONE);
			}
			@Override
			public void onError(DatabaseError error)
			{
				progressBar.setVisibility(View.GONE);
				Utils.trySendFabricReport("SupportTicketsAdapter", error.toException());
			}
		};
	}

	@Override
	protected Class<SupportDetailTicketActivity> getDetailRequestActivityClass()
	{
		return SupportDetailTicketActivity.class;
	}

	@Override
	public void onShowRequestClicked(Ticket request, String requestKey)
	{
		showRequestDetails(request, requestKey);
	}
}
