package com.nimblemind.autoplus;

import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public class SupportTicketFragment extends SupportRequestsFragment<Ticket, SupportTicketViewHolder>
{
	public SupportTicketFragment()
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
	protected int getModelLayoutId()
	{
		return R.layout.support_list_item;
	}

	@Override
	protected Class<Ticket> getModelClass()
	{
		return Ticket.class;
	}

	@Override
	protected Class<SupportTicketViewHolder> getViewHolderClass()
	{
		return SupportTicketViewHolder.class;
	}

	@Override
	protected Query getQuery(DatabaseReference databaseReference)
	{
		return databaseReference.orderByChild("sid").equalTo("");
	}

	@Override
	protected void bindItem(SupportTicketViewHolder viewHolder, Ticket ticket)
	{

		viewHolder.answerRequestButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				// TODO
			}
		});
	}
}
