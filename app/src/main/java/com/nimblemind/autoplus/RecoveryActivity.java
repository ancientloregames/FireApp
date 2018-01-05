package com.nimblemind.autoplus;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/6/2017.
 */

public class RecoveryActivity extends AuthBaseActivity
{
	private TextInputLayout emailContainer;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		auth = FirebaseAuth.getInstance();

		emailContainer = findViewById(R.id.containerEmail);

		final TextView emailView = findViewById(R.id.textEmail);
		View sendButton = findViewById(R.id.buttonSend);

		String email = getIntent().getStringExtra(Constants.EXTRA_EMAIL);
		if (email != null)
		{
			emailView.setText(email);
		}

		sendButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String email = emailView.getText().toString();
				if (validate(email))
				{
					sendResetEmail(email);
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
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void sendResetEmail(final String email)
	{
		if (!Utils.checkInternetConnection(this, true))
			return;

		showInterface(false);
		Log.d("RecoveryActivity", "Sending reset email.");
		auth.sendPasswordResetEmail(email)
				.addOnSuccessListener(new OnSuccessListener<Void>()
				{
					@Override
					public void onSuccess(Void aVoid)
					{
						Log.d("RecoveryActivity", "Email sent.");
						showCompleteDialog(true, email);
					}
				})
				.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Utils.trySendFabricReport("sendResetEmail:failure. Email: " + email, e);
						showCompleteDialog(true, email);
					}
				});
	}

	private void showCompleteDialog(final boolean success, String email)
	{
		new AlertDialog.Builder(this)
				.setMessage(getString(success
					? R.string.textSendResetPassSuccess
					: R.string.textSendResetPassFailure, email))
				.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						if (success)
						{
							setResult(RESULT_OK);
							finish();
						}
						else showInterface(true);
					}
				})
				.create()
				.show();
	}

	private boolean validate(String email)
	{
		boolean result = true;

		emailContainer.setError(null);

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

		return result;
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.activity_recovery;
	}

	@Override
	protected boolean withToolbar()
	{
		return true;
	}
}
