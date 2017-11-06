package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/6/2017.
 */

public class RecoveryActivity extends AuthBaseActivity
{
	private TextInputLayout emailContainer;

	protected FirebaseAuth auth;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		auth = FirebaseAuth.getInstance();

		emailContainer = findViewById(R.id.containerEmail);

		final TextView emailView = findViewById(R.id.textEmail);
		View sendButton = findViewById(R.id.buttonSend);

		String email = getIntent().getStringExtra("email");
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

	private void sendResetEmail(String email)
	{
		if (!Utils.checkInternetConnection(this, true))
			return;

		showInterface(false);
		Log.d("RecoveryActivity", "Sending reset email.");
		auth.sendPasswordResetEmail(email)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Log.d("RecoveryActivity", "Email sent.");
							setResult(RESULT_OK);
							finish();
						}
						else
						{
							showInterface(true);
						}
					}
				});
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
