package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public abstract class DetailRequestActivity<MODEL extends Request> extends AppCompatActivity
{
	protected String uid;

	protected MODEL request;

	protected StorageReference requestStorageRef;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setTitle(getActivityTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		Intent intent = getIntent();

		if (savedInstanceState == null)
		{
			if (!intent.hasExtra("uid") || !intent.hasExtra("request"))
			{
				throw new RuntimeException("Existing uid and request must be passed as an Extra");
			}
		}

		uid = intent.getStringExtra("uid");
		request = intent.getParcelableExtra("request");
		requestStorageRef = FirebaseStorage.getInstance()
				.getReference("photos").child(uid).child(request.storageFolder);

		populateForm(request);
	}

	protected abstract void populateForm(MODEL request);

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

	@LayoutRes
	protected abstract int getLayoutId();

	@StringRes
	protected abstract int getActivityTitle();

	public final static class AutoTextFragment extends Fragment
	{
		public AutoTextFragment()
		{
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.auto_text_info_fragment, container, false);
		}

		@Override
		public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
		{
			super.onViewCreated(view, savedInstanceState);

			Bundle args = getArguments();
			if (args != null)
			{
				((TextView)view.findViewById(R.id.textAutoInfo)).setText(getString(R.string.textAutoInfo,
						args.getString("autoBrand"),
						args.getString("autoModel"),
						args.getInt("year")));
				((TextView)view.findViewById(R.id.textVin)).setText(args.getString("vin"));
			}
			else throw new RuntimeException("Auto info must be passed!");
		}
	}

	public final static class AutoPhotoFragment extends Fragment
	{
		public AutoPhotoFragment()
		{
		}

		@Nullable
		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.auto_photo_info_fragment, container, false);
		}

		@Override
		public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
		{
			super.onViewCreated(view, savedInstanceState);

			Bundle args = getArguments();
			if (args != null)
			{
				String uid = args.getString("uid");
				String requestId = args.getString("requestId");
				StorageReference requestStorageRef = FirebaseStorage.getInstance()
						.getReference("photos").child(uid).child(requestId);

				ViewGroup container = view.findViewById(R.id.ptsPhotoContainer);
				List<String> images = args.getStringArrayList("images");
				for (String image : images)
				{
					ImageView imageView = (ImageView) getLayoutInflater()
							.inflate(R.layout.horizontal_gallery_item, container, false);
					container.addView(imageView);
					GlideApp.with(getContext())
							.load(requestStorageRef.child(image))
							.into(imageView);
				}
			}
			else throw new RuntimeException("Auto info must be passed (uid, requestId, images)!");
		}
	}
}
