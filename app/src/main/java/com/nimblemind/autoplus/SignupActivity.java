package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class SignupActivity extends AuthActivity
{
	private TextInputLayout emailContainer;
	private TextInputLayout nameContainer;
	private TextInputLayout passwordContainer;
	private TextInputLayout passConfirmContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		emailContainer = findViewById(R.id.containerEmail);
		nameContainer = findViewById(R.id.containerName);
		passwordContainer = findViewById(R.id.containerPassword);
		passConfirmContainer = findViewById(R.id.containerPassword2);

		final TextView emailView = findViewById(R.id.textEmail);
		final TextView nameView = findViewById(R.id.textName);
		final TextView passwordView = findViewById(R.id.textPassword);
		final TextView passConfirmView = findViewById(R.id.textPassword2);

		findViewById(R.id.buttonSignup)
				.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String email = emailView.getText().toString();
						String name = nameView.getText().toString();
						String password = passwordView.getText().toString();
						String confirm = passConfirmView.getText().toString();

						if (validate(email, name, password, confirm))
						{
							signUp(email, name, password);
						}
					}
				});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void signUp(final String email, final String name, final String password)
	{
		if (!checkIntenetConnection())
			return;

		Log.d(TAG, "signUp: " + name + " : " + email);
		auth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "onSignUp: success");
							setUserName(name);
							saveCredentials(email, password);
							addUserAndEnter(task.getResult().getUser().getUid(), new User(name, email));
						}
						else
						{
							Log.e(TAG, "onSignUp: failure");
							handleAuthError(task.getException());
						}
					}
				});
	}

	private void setUserName(String name)
	{
		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null)
		{
			UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
					.setDisplayName(name).build();
			currentUser.updateProfile(profileUpdates);
		}
	}

	private void addUserAndEnter(@NonNull String uid, @NonNull User user)
	{
		dbUsers.child(uid).setValue(user);
		enter(uid, user);
	}

	private boolean validate(String email, String name, String password, String confirm)
	{
		boolean result = true;

		emailContainer.setError(null);
		nameContainer.setError(null);
		passwordContainer.setError(null);
		passConfirmContainer.setError(null);

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

		if (name.isEmpty())
		{
			nameContainer.setError(getString(R.string.errorFieldRequired));
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

		if (!password.equals(confirm))
		{
			passConfirmContainer.setError(getString(R.string.errorConfirmPassFailure));
			result = false;
		}

		return result;
	}
}