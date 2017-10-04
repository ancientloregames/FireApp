package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

public class MainActivity extends AppCompatActivity
{
	private final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		User user = (User) getIntent().getSerializableExtra("user");

		Log.d(TAG, user.toString());
	}
}
