package com.nimblemind.autoplus;

import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nimblemind.autoplus.swipereveallayout.SwipeRevealLayout;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public class ClientTicketsFragment extends ClientRequestsFragment<Ticket, ClientTicketViewHolder>
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
		final SwipeRevealLayout swipeLayout = viewHolder.getView();

		binderHelper.bind(swipeLayout, String.valueOf(ticket.id));

		swipeLayout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener()
		{
			@Override
			public void onOpened(SwipeRevealLayout view)
			{
				deleteCandidates.add(viewHolder.getAdapterPosition());
				view.setLockDrag(true);
			}
		});

		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener()
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

		viewHolder.cancelDeletionButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				deleteCandidates.remove(viewHolder.getAdapterPosition());
				swipeLayout.setLockDrag(false);
				swipeLayout.close(true);
			}
		});
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.fragmentClientTicketListName;
	}
}
