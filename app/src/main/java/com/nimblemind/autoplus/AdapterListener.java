package com.nimblemind.autoplus;

/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/26/2017.
 */

interface AdapterListener<MODEL extends Request>
{
	void onShowRequestClicked(MODEL request, String requestKey);
}
