package com.nimblemind.autoplus;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.securepreferences.SecurePreferences;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class LoginActivity extends AuthActivity
{
	static final String EXTRA_NO_AUTOLOGIN = "extra_no_autologin";

	private TextInputLayout emailContainer;
	private TextInputLayout passwordContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		emailContainer = findViewById(R.id.containerEmail);
		passwordContainer = findViewById(R.id.containerPassword);

		final TextView emailView = findViewById(R.id.textEmail);
		final TextView passwordView = findViewById(R.id.textPassword);

		findViewById(R.id.buttonLogin).setOnClickListener(new View.OnClickListener()
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

		findViewById(R.id.buttonPassRecovery).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String email = emailView.getText().toString();
				recoverPassword(!email.isEmpty() && email.matches(".+@.+\\..+")
						? email
						: null);
			}
		});

		boolean noAutologin = getIntent().getBooleanExtra(EXTRA_NO_AUTOLOGIN, false);

		if (!noAutologin && Utils.checkInternetConnection(this, true))
		{
			if (!tryAutoLogin())
			{
				showInterface(true);
			}
		}
		else showInterface(true);
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		handleDeepLink(intent);
	}

	private void handleDeepLink(Intent intent)
	{
		FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
				.addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>()
				{
					@Override
					public void onSuccess(PendingDynamicLinkData data)
					{
						Uri link = data != null ? data.getLink() : null;
						if (link != null)
						{
							String actionCode = link.getQueryParameter("oobCode");
							if (actionCode != null && !actionCode.isEmpty())
							{
								applyActionCode(actionCode);
							}
						}
					}
				})
				.addOnFailureListener(this, new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Utils.trySendFabricReport("getDynamicLink:onFailure", e);
					}
				});
	}

	private void applyActionCode(final String actionCode)
	{
		auth.checkActionCode(actionCode).addOnSuccessListener(new OnSuccessListener<ActionCodeResult>()
		{
			@Override
			public void onSuccess(final ActionCodeResult actionCodeResult)
			{
				auth.applyActionCode(actionCode).addOnSuccessListener(new OnSuccessListener<Void>()
				{
					@Override
					public void onSuccess(Void aVoid)
					{
						switch (actionCodeResult.getOperation())
						{
							case ActionCodeResult.VERIFY_EMAIL:
								showVerifiedDialog();
								break;
							default:
								showInterface(true);
								Log.w(TAG, "applyActionCode: Unknown action code!");
						}
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Utils.trySendFabricReport("verifyEmail:applyActionCode:onFailure", e);
					}
				});
			}
		})
		.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Utils.trySendFabricReport("verifyEmail:checkActionCode:onFailure", e);
			}
		});
	}

	private void showVerifiedDialog()
	{
		AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
				.setTitle(getString(R.string.dialogVerificationSuccessTitle))
				.setMessage(getString(R.string.dialogVerificationMessage))
				.setPositiveButton(getString(R.string.dialogVerificationButtonEnter), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						tryAutoLogin();
					}
				})
				.setCancelable(false)
				.create();
		dialog.show();
	}

	private boolean tryAutoLogin()
	{
		boolean result = true;

		final FirebaseUser currentUser = auth.getCurrentUser();
		if (currentUser != null)
		{
			currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>()
			{
				@Override
				public void onComplete(@NonNull Task<Void> task)
				{
					findUserAndEnter(currentUser);
				}
			});
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
		if (!Utils.checkInternetConnection(this, true))
			return;

		Log.d(TAG, "logIn: " + email);
		showInterface(false);
		auth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>()
				{
					@Override
					public void onComplete(@NonNull Task<AuthResult> task)
					{
						if (task.isSuccessful())
						{
							Log.d(TAG, "logIn: success");
							findUserAndEnter(task.getResult().getUser());
						}
						else
						{
							Utils.handleFirebaseErrorWithToast(LoginActivity.this, "getUserData: failure", task.getException());
							showInterface(true);
						}
					}
				});
	}

	private void findUserAndEnter(@NonNull final FirebaseUser firebaseUser)
	{
		if (!firebaseUser.isEmailVerified() && !"t1@g.com".equals(firebaseUser.getEmail()))//TODO убрать в релизной!
		{
			Toast.makeText(LoginActivity.this, getString(R.string.errorNotVerified), Toast.LENGTH_SHORT).show();
			showInterface(true);
			return;
		}
		final String uid = firebaseUser.getUid();
		Log.d(TAG, "findUserAndEnter. Uid: " + uid);
		dbUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				User user;
				try {
					user = dataSnapshot.getValue(User.class);
				} catch (DatabaseException e) {
					user = null;
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
					showInterface(true);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Utils.handleFirebaseErrorWithToast(LoginActivity.this, "getUserData: failure", databaseError.toException());
				showInterface(true);
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
	}

	private void recoverPassword(@Nullable String email)
	{
		Intent intent = new Intent(this, RecoveryActivity.class);
		intent.putExtra("email", email);
		startActivity(intent);
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.activity_login;
	}

	@Override
	protected boolean withToolbar()
	{
		return false;
	}
}
