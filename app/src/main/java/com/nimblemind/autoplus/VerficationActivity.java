package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/5/2017.
 */

public class VerficationActivity extends AppCompatActivity
{
	private FirebaseUser user;

	private View progressBar;
	private View mainContent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		progressBar = findViewById(R.id.progressBar);
		mainContent = findViewById(R.id.mainContent);

		user = FirebaseAuth.getInstance().getCurrentUser();

		((TextView)findViewById(R.id.textEmail)).setText(user.getEmail());

		findViewById(R.id.buttonRepeat).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				sendEmailVerification(user);
			}
		});

		findViewById(R.id.buttonFinish).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				user.reload().addOnCompleteListener(new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull Task<Void> task)
					{
						if (user.isEmailVerified())
						{
							setResult(RESULT_OK);
							finish();
						}
						else
						{
							Toast.makeText(VerficationActivity.this,
									"Failed to confirm. Try agian",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

		if (user != null)
		{
			sendEmailVerification(user);
		}
		else
		{
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
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

	private void sendEmailVerification(final FirebaseUser user)
	{
		mainContent.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		user.sendEmailVerification()
				.addOnCompleteListener(this, new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull Task<Void> task)
					{
						if (task.isSuccessful())
						{
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									progressBar.setVisibility(View.GONE);
									mainContent.setVisibility(View.VISIBLE);
								}
							});
						}
						else
						{
							Log.e("VerficationActivity", "sendEmailVerification", task.getException());
							Toast.makeText(VerficationActivity.this,
									"Failed to send verification email.",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
	}
}
