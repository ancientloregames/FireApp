package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
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
import io.fabric.sdk.android.Fabric;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

public class AuthActivity extends AppCompatActivity implements SignUpFragment.Listener, Listener
{
	private final String TAG = "AuthActivity";

	static final String EXTRA_NO_AUTOLOGIN = "NoAutoLogIn";

	private FirebaseAuth auth;
	private DatabaseReference dbUsers;

	private View progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		auth = FirebaseAuth.getInstance();

		dbUsers = FirebaseDatabase.getInstance().getReference("users");

		progressBar = findViewById(R.id.progressBarContainer);

		if (savedInstanceState == null)
		{
			onFirstCreate();
		}
	}

	private void onFirstCreate()
	{
		if (BuildConfig.WITH_FABRIC)
		{
			Fabric.with(getApplicationContext(), new Crashlytics());
		}

		boolean internetAvaible = checkIntenetConnection();

		FirebaseUser currentUser = auth.getCurrentUser();
		if (internetAvaible && currentUser != null)
		{
			findUserAndEnter(currentUser.getUid());
		}
		else if (internetAvaible && !getIntent().getBooleanExtra(EXTRA_NO_AUTOLOGIN, false))
		{
			SecurePreferences prefs = new SecurePreferences(this);
			String name = prefs.getString("name", null);
			String email = prefs.getString("email", null);
			String pass = prefs.getString("pass", null);
			UserType type = null;
			try
			{
				type = Enum.valueOf(UserType.class, prefs.getString("type", null));
			}
			catch (NullPointerException | IllegalArgumentException e)
			{
				// Useless exception, if we don't get type, than let it be so
			}

			if (name != null && email != null && pass != null && type != null)
			{
				logInInternal(email, pass);
			}
			else onGotoLogIn();
		}
		else onGotoLogIn();
	}

	private void enter(@NonNull String uid, @NonNull User user)
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

	@Override
	public void onSignUp(final String email, final String name, final String password)
	{
		if (!checkIntenetConnection())
			return;

		progressBar.setVisibility(View.VISIBLE);

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
							saveCredentials(email, name, password, UserType.CLIENT.name());
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
		logInInternal(email, password);
	}

	private void logInInternal(@NonNull final String email, @NonNull final String password)
	{
		if (!checkIntenetConnection())
			return;

		progressBar.setVisibility(View.VISIBLE);

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
							onGotoLogIn();
						}
					}
				});
	}

	@Override
	public void onGotoLogIn()
	{
		progressBar.setVisibility(View.GONE);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentTarget, new LogInFragment())
				.commitNowAllowingStateLoss();
	}

	@Override
	public void onGotoSignUp()
	{
		progressBar.setVisibility(View.GONE);

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
		progressBar.setVisibility(View.VISIBLE);

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
					saveCredentials(user.email, user.name, null, user.type.name());
					enter(uid, user);
				}
				else
				{
					Log.e(TAG, "findUserAndEnter: failure");
					Toast.makeText(AuthActivity.this, getString(R.string.errorAuthInvalidUser),
							Toast.LENGTH_SHORT).show();
					onGotoLogIn();
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

	private void saveCredentials(@Nullable String email, @Nullable String name,
								 @Nullable String password, @Nullable String type)
	{
		final SecurePreferences.Editor editor = new SecurePreferences(AuthActivity.this).edit();

		if(email != null) editor.putString("email", email);
		if(name != null) editor.putString("name", name);
		if(password != null) editor.putString("pass", password);
		if(type != null) editor.putString("type", type);

		editor.apply();
	}

	private void handleAuthError(Exception exception)
	{
		progressBar.setVisibility(View.GONE);

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

	private boolean checkIntenetConnection()
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
