package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


public abstract class NewRequestActivity extends AppCompatActivity implements NewRequestFragment.Listener<Request>
{
	protected enum Mode{ DEFAULT, PHOTO_BASED, TEXT_BASED }
	protected Mode mode = Mode.DEFAULT;

	protected String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_request);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(getActivityTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		if (intent.hasExtra(Constants.EXTRA_UID))
		{
			uid = intent.getStringExtra(Constants.EXTRA_UID);
			if (intent.hasExtra(Constants.EXTRA_TEMPLATE))
			{
				Ticket template = intent.getParcelableExtra(Constants.EXTRA_TEMPLATE);
				mode = template.autoBrand != null ? Mode.TEXT_BASED : Mode.PHOTO_BASED;
			}
		}
		else throw new RuntimeException("The Uid must be passed in order to create new ticket");

		FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			private final TitledFragment[] fragments = getFragments();
			@Override
			public Fragment getItem(int position) {
				return fragments[position].fragment;
			}
			@Override
			public int getCount() {
				return fragments.length;
			}
			@Override
			public CharSequence getPageTitle(int position) {
				return fragments[position].title;
			}
		};

		ViewPager pager = findViewById(R.id.container);
		pager.setAdapter(adapter);
		TabLayout tabLayout = findViewById(R.id.tabs);
		if (mode == Mode.DEFAULT)
		{
			tabLayout.setupWithViewPager(pager);
		}
		else tabLayout.setVisibility(View.GONE);
	}

	@Override
	public void onSubmit(Request request, ArrayList<TitledUri> images)
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra(Constants.EXTRA_REQUEST, request);
		resultIntent.putParcelableArrayListExtra(Constants.EXTRA_IMAGES, images);
		setResult(RESULT_OK, resultIntent);
		finish();
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

	@StringRes
	protected abstract int getActivityTitle();

	protected abstract TitledFragment[] getFragments();

	final class TitledFragment
	{
		String title;
		Fragment fragment;

		TitledFragment(String title, Fragment fragment)
		{
			this.title = title;
			this.fragment = fragment;
		}
	}
}
