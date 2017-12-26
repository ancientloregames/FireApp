package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 12/10/2017.
 */

public class SupportMainActivity extends MainActivity
{

	@Override
	protected RequestsFragment getTicketsFragment()
	{
		return new SupportTicketsFragment();
	}
}