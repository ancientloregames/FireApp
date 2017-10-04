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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.nimblemind.autoplus.LogInFragment.Listener;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */

/* TODO
* Add login
* Save account in DB on create event
* Check ongoing auth in Firebase on onCreate
* Check existing account in AccountManager or SharedPrefference
* */
public class AuthActivity extends AppCompatActivity implements SignUpFragment.Listener, Listener
{
	private static final String TAG = "AuthActivity";

	FirebaseAuth auth;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		auth = FirebaseAuth.getInstance();

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.fragmentTarget, new LogInFragment())
				.commitNowAllowingStateLoss();
	}

	private void enter(@NonNull User user)
	{
		Intent intent = new Intent(AuthActivity.this, MainActivity.class);
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
							String userId = task.getResult().getUser().getUid();
							enter(new User(userId, name, email));
						}
						else
						{
							Exception exception = task.getException();
							if (exception != null)
							{
								exception.printStackTrace();
							}
							if (exception instanceof FirebaseAuthUserCollisionException)
							{
								Toast.makeText(AuthActivity.this, getString(R.string.errorSignupCollision),
										Toast.LENGTH_SHORT).show();
							}
							else
							{
								Toast.makeText(AuthActivity.this, getString(R.string.errorSignupFailure),
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
	}

	@Override
	public void onLogIn(String email, String password)
	{
		Log.d(TAG, "onLogIn: " + email);
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
}
