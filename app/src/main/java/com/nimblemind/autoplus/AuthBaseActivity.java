package com.nimblemind.autoplus;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/6/2017.
 */

public abstract class AuthBaseActivity extends AppCompatActivity
{
	protected View progressBar;
	protected View mainContent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutRes());

		if (withToolbar())
		{
			View statusBarView = findViewById(R.id.statusBarSpace);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
				statusBarView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						App.statusBarHeight));
			}
			else statusBarView.setVisibility(View.GONE);

			Toolbar toolbar = findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			if (getSupportActionBar() != null)
			{
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}

		progressBar = findViewById(R.id.progressBar);
		mainContent = findViewById(R.id.mainContent);

		overridePendingTransition(0, 0);
	}

	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}

	@UiThread
	protected void showInterface(boolean show)
	{
		progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
		mainContent.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@LayoutRes
	protected abstract int getLayoutRes();

	protected abstract boolean withToolbar();
}
