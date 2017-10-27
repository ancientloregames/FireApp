package com.nimblemind.autoplus;

import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.nimblemind.autoplus.swipereveallayout.SwipeRevealLayout;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/26/2017.
 */

class ClientTicketsAdapter extends ClientRequestsAdapter<Ticket, ClientTicketViewHolder>
{
	ClientTicketsAdapter(FirebaseRecyclerOptions<Ticket> options, Listener<Ticket> listener)
	{
		super(options, listener);
	}

	@Override
	protected void bindItem(final ClientTicketViewHolder holder, final Ticket ticket, final String key)
	{
		final SwipeRevealLayout swipeLayout = holder.getView();

		binderHelper.bind(swipeLayout, String.valueOf(ticket.id));

		swipeLayout.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener()
		{
			@Override
			public void onOpened(SwipeRevealLayout view)
			{
				if (listener.get() != null)
				{
					listener.get().onDeleteRequestClicked(key);
					view.setLockDrag(true);
				}
			}
		});

		holder.itemLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (listener.get() != null)
				{
					listener.get().onShowRequestClicked(ticket);
				}
			}
		});

		holder.newRequestButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (listener.get() != null)
				{
					listener.get().onCreateRequestClicked(ticket);
				}
			}
		});

		holder.cancelDeletionButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (listener.get() != null)
				{
					listener.get().onCancelDeleteRequestClicked(key);
					swipeLayout.setLockDrag(false);
					swipeLayout.close(true);
				}
			}
		});
	}

	@Override
	protected int getModelLayoutId()
	{
		return R.layout.client_list_item;
	}

	@Override
	protected ClientTicketViewHolder createViewHolder(View view)
	{
		return new ClientTicketViewHolder(view);
	}
}
