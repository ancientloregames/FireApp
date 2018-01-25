package com.nimblemind.autoplus;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
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

public class SupportDetailTicketActivity extends SupportDetailRequestActivity<Ticket>
{
	private TextView infoView;
	private TextView partView;
	private TextView commentView;
	private AppCompatButton openChatButton;

	private ViewGroup partPhotosContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		infoView = findViewById(R.id.textRequestInfo);
		partView = findViewById(R.id.textAutoPart);
		commentView = findViewById(R.id.textComment);
		partPhotosContainer = findViewById(R.id.partPhotoContainer);
		openChatButton = findViewById(R.id.buttonOpenChat);
	}

	@Override
	protected void populateForm(String ticketId, Ticket ticket)
	{
		infoView.setText(Utils.getDate(ticket.timestamp, "dd MMMM, hh:mm"));

		if (ticket.sid.isEmpty())
		{
			openChatButton.setVisibility(View.GONE);
			AppCompatButton takeRequestButton = findViewById(R.id.buttonTakeRequest);
			takeRequestButton.setVisibility(View.VISIBLE);
			takeRequestButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					takeRequest();
				}
			});
		}
		else openChatButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openChat();
				}
			});

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

		if (ticket.totalMsgs != 0)
		{
			Resources resources = getResources();
			Drawable notificationImage = resources.getDrawable(R.drawable.message_bubble_white_filled);
			int size = (int) openChatButton.getTextSize();
			notificationImage.setBounds(0, 0 , size , size);
			openChatButton.setCompoundDrawables(notificationImage, null, null, null);
			if (ticket.unreadUsrMsgs != 0)
			{
				openChatButton.setText(resources.getString(R.string.textNewMessagesCount, ticket.unreadUsrMsgs));
				openChatButton.setSupportBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.floatingButton)));
			}
			else openChatButton.setText(resources.getString(R.string.textMessagesCount, ticket.totalMsgs));
		}

		Fragment fragment;
		Bundle args = new Bundle();
		if (request.autoBrand != null)
		{
			fragment = new AutoTextFragment();
			args.putString(Constants.EXTRA_REQUEST_AUTOBRAND, request.autoBrand);
			args.putString(Constants.EXTRA_REQUEST_AUTOMODEL, request.autoModel);
			args.putInt(Constants.EXTRA_REQUEST_YEAR, request.year);
			args.putString(Constants.EXTRA_REQUEST_VIN, request.vin);
		}
		else
		{
			fragment = new AutoPhotoFragment();
			args.putString(Constants.DB_REF_USER_ID, request.uid);
			args.putString(Constants.EXTRA_REQUEST_ID, ticketId);
			args.putStringArrayList(Constants.EXTRA_IMAGES, request.ptsPhotos);
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

	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_deteil_ticket_support;
	}
}
