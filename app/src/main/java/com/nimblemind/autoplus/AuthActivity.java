package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nimblemind.autoplus.LogInFragment.Listener;
import com.securepreferences.SecurePreferences;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

public class AuthActivity extends AppCompatActivity implements SignUpFragment.Listener, Listener
{
	private final String TAG = "AuthActivity";

	static final String EXTRA_NO_AUTOLOGIN = "NoAutoLogIn";

	private FirebaseAuth auth;
	private DatabaseReference dbUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		auth = FirebaseAuth.getInstance();

		dbUsers = FirebaseDatabase.getInstance().getReference("users");

		if (savedInstanceState == null)
		{
			onFirstCreate();
		}
	}

	private void onFirstCreate()
	{
		FirebaseUser currentUser = auth.getCurrentUser();

		SecurePreferences prefs = new SecurePreferences(this);
		String name = prefs.getString("name", null);
		String email = prefs.getString("email", null);
		String pass = prefs.getString("pass", null);

		if (currentUser != null)
		{
			findUserAndEnter(currentUser.getUid());
		}
		else if (!getIntent().getBooleanExtra(EXTRA_NO_AUTOLOGIN, false) &&
				name != null && email != null && pass != null)
		{
			logInInternal(email, name, pass);
		}
		else getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.fragmentTarget, new LogInFragment())
					.commitNowAllowingStateLoss();
	}

	private void enter(@NonNull String uid, @NonNull User user)
	{
		Intent intent = new Intent(AuthActivity.this, MainActivity.class);
		intent.putExtra("uid", uid);
		intent.putExtra("user", user);
		startActivity(intent);
		finish();
	}

	@Override
	public void onSignUp(final String email, final String name, final String password)
	{
		Log.d(TAG, "onSignUp: " + name + " : " + email);
		auth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "onSignUp: success");
							saveCredentials(email, name, password);
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

	@Override
	public void onLogIn(String email, String password)
	{
		Log.d(TAG, "onLogIn: " + email);
		logInInternal(email, null, password);
	}

	private void logInInternal(@NonNull final String email, @Nullable final String name, @NonNull final String password)
	{
		auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "onLogIn: success");
							String uid = task.getResult().getUser().getUid();
							if (name != null)
							{
								enter(uid, new User(email, name));
							}
							else findUserAndEnter(uid);
						}
						else
						{
							Log.e(TAG, "onLogIn: failure");
							handleAuthError(task.getException());
						}
					}
				});
	}

	@Override
	public void onGotoLogIn()
	{
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentTarget, new LogInFragment())
				.commitNowAllowingStateLoss();
	}

	@Override
	public void onGotoSignUp()
	{
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentTarget, new SignUpFragment())
				.commitNowAllowingStateLoss();
	}

	private void addUserAndEnter(@NonNull String uid, @NonNull User user)
	{
		dbUsers.child(uid).setValue(user);
		enter(uid, user);
	}

	private void findUserAndEnter(@NonNull final String uid)
	{
		dbUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				User user = dataSnapshot.getValue(User.class);
				if (user != null)
				{
					Log.d(TAG, "findUserAndEnter: success");
					enter(uid, user);
				}
				else
				{
					Log.e(TAG, "findUserAndEnter: failure");
					Toast.makeText(AuthActivity.this, getString(R.string.errorAuthInvalidUser),
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

	private void saveCredentials(String email, String name, String password)
	{
		new SecurePreferences(AuthActivity.this)
				.edit()
				.putString("name", name)
				.putString("email", email)
				.putString("pass", password)
				.apply();
	}

	private void handleAuthError(Exception exception)
	{
		if (exception == null) return;

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
}
