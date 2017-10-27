package com.nimblemind.autoplus;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public class ClientTicketsFragment extends ClientRequestsFragment<Ticket> implements ClientRequestsAdapter.Listener<Ticket>
{
	public ClientTicketsFragment()
	{
	}

	@Override
	protected Class<NewTicketActivity> getNewRequestActivityClass()
	{
		return NewTicketActivity.class;
	}

	@Override
	protected Class<ClientTicketActivity> getDetailsRequestActivityClass()
	{
		return ClientTicketActivity.class;
	}

	@Override
	protected int getFragmentLayoutId()
	{
		return R.layout.client_list_fragment;
	}

	@Override
	protected Class<Ticket> getModelClass()
	{
		return Ticket.class;
	}

	@Override
	protected Query getQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild("uid").equalTo(uid);
	}

	@Override
	protected RequestsAdapter createAdapter(FirebaseRecyclerOptions<Ticket> options)
	{
		return new ClientTicketsAdapter(options, this);
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.fragmentClientTicketListName;
	}

	@Override
	public void onShowRequestClicked(Ticket request, String requestKey)
	{
		showRequestDetails(request, requestKey);
	}

	@Override
	public void onCreateRequestClicked(Ticket request)
	{
		createNewRequest(request);
	}

	@Override
	public void onDeleteRequestClicked(String key)
	{
		deleteCandidates.add(key);
	}

	@Override
	public void onCancelDeleteRequestClicked(String key)
	{
		deleteCandidates.remove(key);
	}
}
