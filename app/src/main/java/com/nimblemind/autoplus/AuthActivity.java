package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.securepreferences.SecurePreferences;
import io.fabric.sdk.android.Fabric;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

public abstract class AuthActivity extends AppCompatActivity
{
	protected final String TAG = "AuthActivity";

	protected FirebaseAuth auth;
	protected DatabaseReference dbUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		auth = FirebaseAuth.getInstance();

		dbUsers = FirebaseDatabase.getInstance().getReference("users");
	}

	protected void enter(@NonNull String uid, @NonNull User user)
	{
		Class mainActivityClass = null;
		switch (user.type)
		{
			case CLIENT:
				mainActivityClass = ClientMainActivity.class;
				break;
			case SUPPORT:
				// TODO
				break;
			default:
				throw new RuntimeException("Wrong User Type!");
		}
		Intent intent = new Intent(AuthActivity.this, mainActivityClass);
		intent.putExtra("uid", uid);
		intent.putExtra("user", user);
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

	protected void handleAuthError(Exception exception)
	{
		if (exception == null) return;

		if (Fabric.isInitialized())
		{
			Crashlytics.logException(exception);
		}

		exception.printStackTrace();
		String message;
		if (exception instanceof FirebaseAuthUserCollisionException)
		{
			message = getString(R.string.errorAuthCollision);
		}
		else if (exception instanceof FirebaseAuthInvalidUserException)
		{
			message = getString(R.string.errorAuthInvalidUser);
		}
		else if (exception instanceof FirebaseAuthRecentLoginRequiredException)
		{
			message = getString(R.string.errorAuthRecentLogin);
		}
		else if (exception instanceof FirebaseAuthEmailException)
		{
			message = getString(R.string.errorAuthEmail);
		}
		else if (exception instanceof FirebaseAuthInvalidCredentialsException)
		{
			message = getString(R.string.errorAuthInvalidCredentials);
		}
		else if (exception instanceof DatabaseException)
		{
			message = getString(R.string.errorAythDatabase);
		}
		else
		{
			message = getString(R.string.errorAuthGeneral);
		}
		Toast.makeText(AuthActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	protected boolean checkIntenetConnection()
	{
		boolean result = true;
		if (!Utils.isNetworkAvailable(this))
		{
			Toast.makeText(this, getString(R.string.errorNoInternetConnection), Toast.LENGTH_SHORT).show();
			result = false;
		}

		return result;
	}
}
