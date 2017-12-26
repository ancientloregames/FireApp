package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/29/2017.
 */

public class ClientMainActivity extends MainActivity
{

	@Override
	protected RequestsFragment getTicketsFragment()
	{
		return new ClientTicketsFragment();
	}
}
