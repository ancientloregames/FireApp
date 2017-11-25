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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import static com.nimblemind.autoplus.LoginActivity.EXTRA_NO_AUTOLOGIN;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

public abstract class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
	private final String TAG = MainActivity.class.getSimpleName();

	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;

	private String uid;

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

		uid = getIntent().getStringExtra("uid");
		User user = (User) getIntent().getSerializableExtra("user");

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

		Log.d(TAG, user.toString() + " uid: " + uid);

		if (savedInstanceState == null)
		{
			onNavigationItemSelected(navigationView.getMenu().findItem(R.id.navTicketFragment));
		}
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
		RequestsFragment fragment = null;
		int id = item.getItemId();
		switch (id)
		{
			case R.id.navTicketFragment:
				fragment = getTicketsFragment();
				break;
			case R.id.navOfferFragment:
				// TODO
				break;
			case R.id.navOrderFragment:
				// TODO
				break;
		}

		drawer.closeDrawer(GravityCompat.START);

		if (fragment != null)
		{
			Bundle arguments = new Bundle();
			arguments.putString("uid", uid);
			fragment.setArguments(arguments);
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.fragmentTarget, fragment)
					.commitNowAllowingStateLoss();
			return true;
		}
		else return super.onOptionsItemSelected(item);
	}

	private void gotoAuthActivity()
	{
		FirebaseAuth.getInstance().signOut();
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.putExtra(EXTRA_NO_AUTOLOGIN, true);
		startActivity(intent);
		finish();
	}

	protected abstract RequestsFragment getTicketsFragment();
}
