package com.nimblemind.autoplus;

import android.content.Intent;
import android.view.View;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public class ClientTicketsFragment extends ClientRequestsFragment<Ticket> implements ClientRequestsAdapter.Listener<Ticket>
{
	public ClientTicketsFragment()
	{
	}

	@Override
	protected String sendRequest(Ticket ticket, ArrayList<TitledUri> images)
	{
		String ticketId = super.sendRequest(ticket, images);

		getActivity().startService(new Intent(getActivity(), ImageUploadService.class)
				.putParcelableArrayListExtra(ImageUploadService.EXTRA_IMAGES, images)
				.putExtra(ImageUploadService.EXTRA_PATH, new String[] {uid, ticketId})
				.setAction(ImageUploadService.ACTION_UPLOAD));
		return ticketId;
	}

	@Override
	protected Class<NewTicketActivity> getNewRequestActivityClass()
	{
		return NewTicketActivity.class;
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
	protected Query getInitialQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild(Constants.DB_REF_USER_ID).equalTo(uid);
	}

	@Override
	protected RequestsAdapter createAdapter(FirebaseRecyclerOptions<Ticket> options)
	{
		return new ClientTicketsAdapter(options, this){
			@Override
			public void onDataChanged()
			{
				progressBar.setVisibility(View.GONE);
			}
			@Override
			public void onError(DatabaseError error)
			{
				progressBar.setVisibility(View.GONE);
				Utils.trySendFabricReport("ClientTicketsAdapter", error.toException());
			}
		};
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.fragmentClientTicketListName;
	}

	@Override
	public void onShowRequestClicked(Ticket request,String requstId)
	{
		showRequestDetails(request, requstId);
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

	@Override
	protected Class<ClientDetailTicketActivity> getDetailRequestActivityClass()
	{
		return ClientDetailTicketActivity.class;
	}
}
