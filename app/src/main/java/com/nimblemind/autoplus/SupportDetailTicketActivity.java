package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 12/17/2017.
 */

public class SupportDetailTicketActivity extends DetailRequestActivity<Ticket>
{
	private TextView infoView;
	private TextView partView;
	private TextView commentView;

	private ViewGroup partPhotosContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		infoView = findViewById(R.id.textRequestInfo);
		partView = findViewById(R.id.textAutoPart);
		commentView = findViewById(R.id.textComment);
		partPhotosContainer = findViewById(R.id.partPhotoContainer);

		findViewById(R.id.buttonAnswerRequest).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				answerRequest();
			}
		});
	}

	@Override
	protected void populateForm(Ticket ticket)
	{
		infoView.setText(Utils.getDate(ticket.timestamp, "dd MMMM, hh:mm"));

		String sparePart = ticket.spareParts.get(0);
		if (!sparePart.isEmpty())
		{
			partView.setText(sparePart);
		}
		else ((View)partView.getParent()).setVisibility(View.GONE);

		if (!ticket.comment.isEmpty())
		{
			commentView.setText(ticket.comment);
			commentView.setVisibility(View.VISIBLE);
		}

		Fragment fragment;
		Bundle args = new Bundle();
		if (request.autoBrand != null)
		{
			fragment = new AutoTextFragment();
			args.putString("autoBrand", request.autoBrand);
			args.putString("autoModel", request.autoModel);
			args.putInt("year", request.year);
			args.putString("vin", request.vin);
		}
		else
		{
			fragment = new AutoPhotoFragment();
			args.putString("uid", request.uid);
			args.putString("requestId", request.storageFolder);
			args.putStringArrayList("images", request.ptsPhotos);
		}
		fragment.setArguments(args);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.autoInfoContainer, fragment)
				.commitNowAllowingStateLoss();

		for (String image : ticket.partPhotos)
		{
			final View imageLayout = getLayoutInflater()
					.inflate(R.layout.horizontal_gallery_item, partPhotosContainer, false);
			final ImageView imageView = imageLayout.findViewById(R.id.image);
			partPhotosContainer.addView(imageLayout);
			GlideApp.with(this)
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
									Utils.openImage(SupportDetailTicketActivity.this, file);
								}
							});
						}
					});
		}
	}

	protected void answerRequest()
	{
		Intent resultIntent = new Intent();
		resultIntent.putExtra("requestId", requestId);
		resultIntent.putExtra("request", request);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_deteil_ticket_support;
	}
}
