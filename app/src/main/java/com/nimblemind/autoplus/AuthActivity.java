package com.nimblemind.autoplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.nimblemind.autoplus.LogInFragment.Listener;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

public class AuthActivity extends AppCompatActivity implements SignUpFragment.Listener, Listener
{
	private static final String TAG = "AuthActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragmentTarget,new LogInFragment())
				.commitNowAllowingStateLoss();
	}

	@Override
	public void onSignUp(String email, String name, String password)
	{
		Log.d(TAG, "onSignUp: " + name + " : " + email);
	}

	@Override
	public void onLogIn(String email, String password)
	{
		Log.d(TAG, "onLogIn: " + email);
	}
}
