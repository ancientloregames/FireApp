package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.securepreferences.SecurePreferences;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class LoginActivity extends AuthActivity
{
	private TextInputLayout emailContainer;
	private TextInputLayout passwordContainer;

	private static boolean firstStart = true;

	private boolean initialized;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (firstStart && checkIntenetConnection())
		{
			firstStart = false;
			if (!tryAutoLogin())
			{
				initialize();
			}
		}
		else initialize();
	}

	private void initialize()
	{
		if (initialized)
			return;

		initialized = true;

		setContentView(R.layout.activity_login);

		emailContainer = findViewById(R.id.containerEmail);
		passwordContainer = findViewById(R.id.containerPassword);

		final TextView emailView = findViewById(R.id.textEmail);
		final TextView passwordView = findViewById(R.id.textPassword);

		findViewById(R.id.buttonLogin)
				.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String email = emailView.getText().toString();
						String password = passwordView.getText().toString();

						if (validate(email, password))
						{
							logIn(email, password);
						}
					}
				});

		findViewById(R.id.buttonSignup).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				signUp();
			}
		});
	}

	private boolean tryAutoLogin()
	{
		boolean result = true;

		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null)
		{
			findUserAndEnter(currentUser.getUid());
		}
		else
		{
			SecurePreferences prefs = new SecurePreferences(this);
			String email = prefs.getString("email", null);
			String pass = prefs.getString("pass", null);

			if (email != null && pass != null)
			{
				logIn(email, pass);
			}
			else result = false;
		}

		return result;
	}

	private void logIn(@NonNull String email, @NonNull String password)
	{
		if (!checkIntenetConnection())
			return;

		Log.d(TAG, "logIn: " + email);
		auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "logIn: success");
							findUserAndEnter(task.getResult().getUser().getUid());
						}
						else
						{
							Log.e(TAG, "logIn: failure");
							handleAuthError(task.getException());
						}
					}
				});
	}

	private void findUserAndEnter(@NonNull final String uid)
	{
		Log.d(TAG, "findUserAndEnter. Uid: " + uid);
		dbUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				User user = null;
				try {
					user = dataSnapshot.getValue(User.class);
				} catch (DatabaseException e) {
					// User data in database was somehow corrupted
				}
				if (user != null)
				{
					Log.d(TAG, "findUserAndEnter: success");
					saveCredentials(user.email, null);
					enter(uid, user);
				}
				else
				{
					Log.e(TAG, "findUserAndEnter: failure");
					Toast.makeText(LoginActivity.this, getString(R.string.errorAuthInvalidUser),
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Log.e(TAG, "getUserData: failure");
				handleAuthError(databaseError.toException());
			}
		});
	}

	private boolean validate(String email, String password)
	{
		boolean result = true;

		emailContainer.setError(null);
		passwordContainer.setError(null);

		if (email.isEmpty())
		{
			emailContainer.setError(getString(R.string.errorFieldRequired));
			result = false;
		}
		else if (!email.matches(".+@.+\\..+"))
		{
			emailContainer.setError(getString(R.string.errorInvalidEmail));
			result = false;
		}

		if (password.isEmpty())
		{
			passwordContainer.setError(getString(R.string.errorFieldRequired));
			result = false;
		}
		else if (password.length() < 6)
		{
			passwordContainer.setError(getString(R.string.errorInvalidPassword));
			result = false;
		}

		return result;
	}

	private void signUp()
	{
		Intent intent = new Intent(this, SignupActivity.class);
		startActivity(intent);
		finish();
	}
}
