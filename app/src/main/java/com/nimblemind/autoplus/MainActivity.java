package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;


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

		findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				FirebaseAuth.getInstance().signOut();
				Intent intent = new Intent(MainActivity.this, AuthActivity.class);
				intent.putExtra(AuthActivity.EXTRA_NO_AUTOLOGIN, true);
				startActivity(intent);
				finish();
			}
		});
	}
}
