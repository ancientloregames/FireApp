package com.nimblemind.autoplus;

import android.content.Context;
import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public class ClientTicketsFragment extends ClientRequestsFragment<Ticket, ClientTicketViewHolder>
{
	public ClientTicketsFragment()
	{
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		getActivity().setTitle(R.string.fragmentClientTicketListName);
	}

	@Override
	protected Class<NewTicketActivity> getNewRequestActivityClass()
	{
		return NewTicketActivity.class;
	}

	@Override
	protected int getFragmentLayoutId()
	{
		return R.layout.fragment_clientlist;
	}

	@Override
	protected int getModelLayoutId()
	{
		return R.layout.clientlist_item;
	}

	@Override
	protected Class<Ticket> getModelClass()
	{
		return Ticket.class;
	}

	@Override
	protected Class<ClientTicketViewHolder> getViewHolderClass()
	{
		return ClientTicketViewHolder.class;
	}

	@Override
	protected Query getQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild("uid").equalTo(uid);
	}

	@Override
	protected void bindItem(final ClientTicketViewHolder viewHolder, final Ticket ticket)
	{
		viewHolder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				showRequestDetails(ticket);
			}
		});

		viewHolder.newRequestButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createNewRequest(ticket);
			}
		});

		viewHolder.deleteRequestButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				deleteRequest(viewHolder.getAdapterPosition());
			}
		});
	}
}
