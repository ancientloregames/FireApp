package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

/* TODO
* Check existing account in AccountManager or SharedPrefference
* */
public class AuthActivity extends AppCompatActivity implements SignUpFragment.Listener, Listener
{
	private final String TAG = "AuthActivity";

	private FirebaseAuth auth;
	private DatabaseReference dbUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		auth = FirebaseAuth.getInstance();

		dbUsers = FirebaseDatabase.getInstance().getReference("users");

		FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null)
		{
			findUserAndEnter(currentUser.getUid());
		}
		else if (savedInstanceState == null)
		{
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.fragmentTarget, new LogInFragment())
					.commitNowAllowingStateLoss();
		}
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
	public void onLogIn(final String email, final String password)
	{
		Log.d(TAG, "onLogIn: " + email);
		auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "onLogIn: success");
							findUserAndEnter(task.getResult().getUser().getUid());
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
