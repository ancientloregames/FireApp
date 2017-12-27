package com.nimblemind.autoplus;

import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerOptions;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/27/2017.
 */

class SupportTicketsAdapter extends SupportRequestsAdapter<Ticket, SupportTicketViewHolder>
{
	SupportTicketsAdapter(FirebaseRecyclerOptions<Ticket> options, Listener<Ticket> listener)
	{
		super(options, listener);
	}

	@Override
	protected void bindItem(final SupportTicketViewHolder holder, final Ticket ticket)
	{
		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (listener.get() != null)
				{
					String requestId = getRef(holder.getAdapterPosition()).getKey();
					listener.get().onShowRequestClicked(ticket, requestId);
				}
			}
		});

		holder.answerRequestButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (listener.get() != null)
				{
					String key = getRef(holder.getAdapterPosition()).getKey();
					listener.get().onAnswerRequestClicked(ticket, key);
				}
			}
		});
	}

	@Override
	protected int getModelLayoutId()
	{
		return R.layout.support_list_item;
	}

	@Override
	protected SupportTicketViewHolder createViewHolder(View view)
	{
		return new SupportTicketViewHolder(view);
	}
}
