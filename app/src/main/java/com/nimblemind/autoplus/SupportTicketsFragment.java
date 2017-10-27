package com.nimblemind.autoplus;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


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
	protected Query getQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild("sid").equalTo("");
	}

	@Override
	protected RequestsAdapter createAdapter(FirebaseRecyclerOptions<Ticket> options)
	{
		return new SupportTicketsAdapter(options, this);
	}

	@Override
	protected Class<SupportTicketActivity> getDetailsRequestActivityClass()
	{
		return SupportTicketActivity.class;
	}

	@Override
	public void onShowRequestClicked(Ticket request)
	{
		showRequestDetails(request);
	}
}
