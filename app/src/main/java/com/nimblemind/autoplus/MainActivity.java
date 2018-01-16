package com.nimblemind.autoplus;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import static com.nimblemind.autoplus.Constants.EXTRA_NO_AUTOLOGIN;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

public abstract class MainActivity<FRAGMENT extends RequestsFragment> extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;

	private String uid;

	private User user;

	protected FRAGMENT fragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawer = findViewById(R.id.drawer);
		drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawerOpen, R.string.drawerClose);
		drawer.addDrawerListener(drawerToggle);
		drawerToggle.syncState();

		NavigationView navigationView = findViewById(R.id.navigator);
		navigationView.setNavigationItemSelectedListener(this);

		uid = getIntent().getStringExtra(Constants.EXTRA_UID);
		user = (User) getIntent().getSerializableExtra(Constants.EXTRA_USER);

		((TextView)navigationView.findViewById(R.id.textEmail)).setText(user.email);
		navigationView.findViewById(R.id.buttonLogOut).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				drawer.closeDrawer(GravityCompat.START, false);
				gotoAuthActivity();
			}
		});

		onNavigationItemSelected(navigationView.getMenu().findItem(R.id.navTicketFragment));
	}

	@Override
	public void onBackPressed()
	{
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else super.onBackPressed();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{
		drawer.closeDrawer(GravityCompat.START);

		FRAGMENT fragment;
		switch (item.getItemId())
		{
			case R.id.navTicketFragment:
				fragment = getTicketsFragment();
				break;
			// TODO case R.id.navOfferFragment:
			// TODO case R.id.navOrderFragment:
			case R.id.navBranchesFragment:
				Intent intent = new Intent(this, CompanyInfoActivity.class);
				startActivity(intent);
				return true;
			default: return super.onOptionsItemSelected(item);
		}

		switchFragment(fragment, getFragmentArguments());

		return true;
	}

	private void gotoAuthActivity()
	{
		FirebaseAuth.getInstance().signOut();
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.putExtra(EXTRA_NO_AUTOLOGIN, true);
		startActivity(intent);
		finish();
	}

	protected void switchFragment(FRAGMENT fragment, Bundle arguments)
	{
		this.fragment = fragment;
		fragment.setArguments(arguments);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragmentTarget, fragment)
				.commitNowAllowingStateLoss();
	}

	protected Bundle getFragmentArguments()
	{
		Bundle arguments = new Bundle();
		arguments.putString(Constants.EXTRA_UID, uid);
		arguments.putString(Constants.EXTRA_USER_NAME, user.name);
		return arguments;
	}

	protected abstract FRAGMENT getTicketsFragment();
}
