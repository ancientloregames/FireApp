package com.nimblemind.autoplus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


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
	}

	@Override
	protected void populateForm(Ticket ticket)
	{
		infoView.setText(Utils.getDate(ticket.timestamp, "dd MMMM, hh:mm"));
		partView.setText(ticket.partType);
		commentView.setText(ticket.comment);

		Fragment fragment;
		Bundle args = new Bundle();
		if (request.autoName != null)
		{
			fragment = new AutoTextFragment();
			args.putString("name", request.autoName);
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
			ImageView imageView = (ImageView) getLayoutInflater()
					.inflate(R.layout.horizontal_gallery_item, partPhotosContainer,false);
			partPhotosContainer.addView(imageView);
			GlideApp.with(this)
					.load(requestStorageRef.child(image))
					.into(imageView);
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
