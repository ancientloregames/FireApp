package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/25/2017.
 */

public abstract class DetailRequestActivity<MODEL extends Request> extends AppCompatActivity
{
	public final static String ACTION_OPEN_CHAT = "action_open_chat";
	public final static String ACTION_NEW_REQUEST = "action_new_request";

	protected String uid;

	protected String requestId;

	protected MODEL request;

	protected StorageReference requestStorageRef;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
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
		requestId = intent.getStringExtra("requestId");
		request = intent.getParcelableExtra("request");
		requestStorageRef = FirebaseStorage.getInstance()
				.getReference("photos").child(request.uid).child(request.storageFolder);

		setTitle(getString(R.string.activityDetailTicketTitle, request.id));
		populateForm(request);
	}

	protected void openChat()
	{
		Intent intent = new Intent();
		intent.putExtra("requestId", requestId);
		intent.putExtra("request", request);
		intent.setAction(ACTION_OPEN_CHAT);
		setResult(RESULT_OK, intent);
		finish();
	}

	protected void createNewOnThis()
	{
		Intent intent = new Intent();
		intent.putExtra("request", request);
		intent.setAction(ACTION_NEW_REQUEST);
		setResult(RESULT_OK, intent);
		finish();
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
				((TextView)view.findViewById(R.id.textAutoInfo)).setText(String.format("%s %s, %d",
						args.getString("autoBrand"),
						args.getString("autoModel"),
						args.getInt("year")));
				((TextView)view.findViewById(R.id.textVin)).setText(getString(R.string.textAutoVin,
					args.getString("vin")));
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
					final View imageLayout = getLayoutInflater()
							.inflate(R.layout.horizontal_gallery_item, container, false);
					final ImageView imageView = imageLayout.findViewById(R.id.image);
					container.addView(imageLayout);
					GlideApp.with(getContext())
							.asFile()
							.load(requestStorageRef.child(image))
							.into(new SimpleTarget<File>()
							{
								@Override
								public void onResourceReady(final File file, Transition<? super File> transition)
								{
									imageView.setImageURI(Uri.fromFile(file));
									imageView.setOnClickListener(new View.OnClickListener()
									{
										@Override
										public void onClick(View v)
										{
											Utils.openImage(getActivity(), file);
										}
									});
								}
							});
				}
			}
			else throw new RuntimeException("Auto info must be passed (uid, requestId, images)!");
		}
	}
}
