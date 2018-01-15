package com.nimblemind.autoplus;

import android.content.Intent;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 1/15/2018.
 */

public abstract class SupportDetailRequestActivity<REQUEST extends Request> extends DetailRequestActivity<REQUEST>
{
	public static final String ACTION_OPEN_CHAT = "action_open_chat";
	public static final String ACTION_TAKE_REQUEST = "action_take_request";

	protected void openChat()
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, requestId);
		resultIntent.putExtra(Constants.EXTRA_REQUEST, request);
		resultIntent.setAction(ACTION_OPEN_CHAT);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	protected void takeRequest()
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Constants.EXTRA_REQUEST_ID, requestId);
		resultIntent.putExtra(Constants.EXTRA_REQUEST, request);
		resultIntent.setAction(ACTION_TAKE_REQUEST);
		setResult(RESULT_OK, resultIntent);
		finish();
	}
}
