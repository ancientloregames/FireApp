package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.securepreferences.SecurePreferences;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

public abstract class AuthActivity extends AuthBaseActivity
{
	protected final String TAG = "AuthActivity";
	protected DatabaseReference dbUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		dbUsers = FirebaseDatabase.getInstance().getReference(Constants.DB_REF_USERS);
	}

	protected void enter(@NonNull String uid, @NonNull User user)
	{
		Class mainActivityClass;
		switch (user.type)
		{
			case CLIENT:
				mainActivityClass = ClientMainActivity.class;
				break;
			case SUPPORT:
				mainActivityClass = SupportMainActivity.class;
				break;
			default:
				throw new RuntimeException("Wrong User Type!");
		}
		Intent intent = new Intent(AuthActivity.this, mainActivityClass);
		intent.putExtra(Constants.EXTRA_UID, uid);
		intent.putExtra(Constants.EXTRA_USER, user);
		startActivity(intent);
		finish();
	}

	protected void saveCredentials(@Nullable String email, @Nullable String password)
	{
		final SecurePreferences.Editor editor = new SecurePreferences(AuthActivity.this).edit();

		if(email != null) editor.putString("email", email);
		if(password != null) editor.putString("pass", password);

		editor.apply();
	}
}
