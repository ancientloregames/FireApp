package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseUser;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class VerificationActivity extends AuthBaseActivity
{
	private TextView infoView;
	private View repeatButton;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final FirebaseUser user = auth.getCurrentUser();

		if (user != null)
		{
			final ActionCodeSettings settings = ActionCodeSettings.newBuilder()
					.setUrl(Constants.PROJECT_LINK + "verify?uid=" + user.getUid())
					.setAndroidPackageName(getPackageName(), false, null)
					.setHandleCodeInApp(true)
					.build();

			infoView = findViewById(R.id.textInfo);

			((TextView)findViewById(R.id.textEmail)).setText(user.getEmail());

			repeatButton = findViewById(R.id.buttonRepeat);
			repeatButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					sendEmailVerification(user, settings);
				}
			});

			findViewById(R.id.buttonFinish).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					setResult(RESULT_OK);
					finish();
				}
			});
			sendEmailVerification(user, settings);
		}
		else
		{
			setResult(RESULT_CANCELED);
			finish();
		}
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

	private void sendEmailVerification(final FirebaseUser user, ActionCodeSettings settings)
	{
		showInterface(false);
		user.sendEmailVerification(settings).addOnCompleteListener(this, new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull final Task<Void> task)
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (task.isSuccessful())
								{
									infoView.setText(getString(R.string.textVerificationSuccessMessage));
									repeatButton.setVisibility(View.GONE);
								}
								else
								{
									infoView.setText(getString(R.string.textVerificationFailedMessage));
									repeatButton.setVisibility(View.VISIBLE);
									Utils.trySendFabricReport("sendEmailVerification:onFailure", task.getException());
								}
								showInterface(true);
							}
						});
					}
				});
	}

	@Override
	protected int getLayoutRes()
	{
		return R.layout.activity_verification;
	}

	@Override
	protected boolean withToolbar()
	{
		return true;
	}
}
