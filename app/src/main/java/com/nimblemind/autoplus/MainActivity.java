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
import com.google.firebase.auth.FirebaseAuth;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

public class MainActivity extends AppCompatActivity implements RequestsFragment.Listener,
		NavigationView.OnNavigationItemSelectedListener
{
	private final String TAG = MainActivity.class.getSimpleName();

	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;

	private String uid;
	private UserType userType;

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
		userType = user.type;

		Log.d(TAG, user.toString() + " uid: " + uid);

//		new AlertDialog.Builder(this)
//				.setTitle("Successful enter")
//				.setMessage("uid: " + uid + "\n" + user.toString())
//				.setPositiveButton("OK", null)
//				.create()
//				.show();

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
		else
		{
			super.onBackPressed();
		}
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
				if (userType == UserType.CLIENT)
				{
					fragment = new ClientTicketsFragment();
				}
				else if (userType == UserType.SUPPORT)
				{
					fragment = new SupportTicketFragment();
				}
				break;
			case R.id.navOfferFragment:
				// TODO
				break;
			case R.id.navOrderFragment:
				// TODO
				break;
			case R.id.navLogOut:
				drawer.closeDrawer(GravityCompat.START, false);
				gotoAuthActivity(true);
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

	private void gotoAuthActivity(final boolean noAutologin)
	{
		FirebaseAuth.getInstance().signOut();
		Intent intent = new Intent(MainActivity.this, AuthActivity.class);
		intent.putExtra(AuthActivity.EXTRA_NO_AUTOLOGIN, noAutologin);
		startActivity(intent);
		finish();
	}
}
