package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

public class MainActivity extends AppCompatActivity implements RequestsFragment.Listener
{
	private final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String uid = getIntent().getStringExtra("uid");
		User user = (User) getIntent().getSerializableExtra("user");

		Log.d(TAG, user.toString() + " uid: " + uid);

		new AlertDialog.Builder(this)
				.setTitle("Successful enter")
				.setMessage("uid: " + uid + "\n" + user.toString())
				.setPositiveButton("OK", null)
				.create()
				.show();

		if (savedInstanceState == null)
		{
			onFirstCreate(uid, user);
		}
	}

	private void onFirstCreate(String uid, User user)
	{
		RequestsFragment fragment = null;

		switch (user.type)
		{
			case CLIENT:
				fragment = new ClientTicketsFragment();
				break;
			case SUPPORT:
				// TODO Сделать инфраструктуру для учетной записи сотрудника
				break;
			default:
				// NOTE Такого не может произойти, так ведь? Возвращаемся на форму авторизации
				gotoAuthActivity(true);
				return;
		}

		Bundle arguments = new Bundle();
		arguments.putString("uid", uid);
		fragment.setArguments(arguments);

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragmentTarget, fragment)
				.commitNowAllowingStateLoss();
	}

	private void gotoAuthActivity(final boolean noAutologin)
	{
		FirebaseAuth.getInstance().signOut();
		Intent intent = new Intent(MainActivity.this, AuthActivity.class);
		intent.putExtra(AuthActivity.EXTRA_NO_AUTOLOGIN, noAutologin);
		startActivity(intent);
		finish();
	}

	@Override
	public void onLogout()
	{
		gotoAuthActivity(true);
	}
}
