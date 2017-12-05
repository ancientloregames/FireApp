package com.nimblemind.autoplus;

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


public class DetailTicketActivity extends DetailRequestActivity<Ticket>
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

		findViewById(R.id.newRequestButton).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				createNewOnThis();
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
			args.putString("uid", uid);
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
			final ImageView imageView = (ImageView) getLayoutInflater()
					.inflate(R.layout.horizontal_gallery_item, partPhotosContainer,false);
			partPhotosContainer.addView(imageView);
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
									Utils.openImage(DetailTicketActivity.this, file);
								}
							});
						}
					});
		}
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_detail_ticket;
	}

	@Override
	protected int getActivityTitle()
	{
		return R.string.activityDetailTicket;
	}
}
