package com.nimblemind.autoplus;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/11/2017.
 */

public class ClientTicketListFragment extends RequestListFragment<Ticket, ClientTicketViewHolder>
{
	public ClientTicketListFragment()
	{
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
		return databaseReference.orderByChild("uid").equalTo(getUid());
	}

	@Override
	protected void setButtonListeners(ClientTicketViewHolder viewHolder, Ticket ticket)
	{
		// TODO Сделать обработку нажатий на кнопки
		viewHolder.notificationView.setOnClickListener(null);
		viewHolder.newRequestButton.setOnClickListener(null);
		viewHolder.deleteRequestButton.setOnClickListener(null);
	}
}
