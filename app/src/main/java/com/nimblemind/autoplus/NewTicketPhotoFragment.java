package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/19/2017.
 */

public class NewTicketPhotoFragment extends NewRequestFragment<Ticket>
{
	protected static final int INTENT_ADD_PTS_PHOTO = 102;

	private TextInputLayout partContainer;
	private TextInputLayout commentContainer;

	private ViewGroup partPhotosContainer;
	private ViewGroup ptsPhotosContainer;

	private TextView partView;
	private TextView commentView;

	protected final int maxPtsPhotoCount = 1;

	protected ArrayList<TitledUri> ptsPhotos = new ArrayList<>(maxPtsPhotoCount);

	protected ArrayList<String> ptsPhotosNames = new ArrayList<>(maxPtsPhotoCount);

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (view != null)
		{
			partContainer = view.findViewById(R.id.containerPart);
			commentContainer = view.findViewById(R.id.containerComment);

			partPhotosContainer = view.findViewById(R.id.containerPartPhotos);
			ptsPhotosContainer = view.findViewById(R.id.containerPTSPhotos);

			partView = view.findViewById(R.id.textPart);
			commentView = view.findViewById(R.id.textComment);

			partView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openList("parts", INTENT_SPARE_PART);
				}
			});

			view.findViewById(R.id.buttonAddPTSPhoto).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					openGallery(INTENT_ADD_PTS_PHOTO);
				}
			});

			view.findViewById(R.id.buttonAddImage).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					openGallery(INTENT_ADD_PART_PHOTO);
				}
			});

			view.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					tryCreateRequest();
				}
			});
		}

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == INTENT_ADD_PTS_PHOTO)
			{
				Uri imageUri = data.getData();
				if (imageUri != null)
				{
					String name = String.valueOf(System.currentTimeMillis());
					ptsPhotos.add(new TitledUri(name, imageUri));
					ptsPhotosNames.add(name);
					onPtsPhotoRecieved(imageUri);
				}
			}
			else if (requestCode == INTENT_SPARE_PART)
			{
				partView.setText(data.getStringExtra("result"));
			}
		}
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.new_ticket_photo_fragment;
	}

	@Override
	protected void populateWithTemplate(@NonNull Ticket template)
	{
		// TODO compile 'com.firebaseui:firebase-ui-storage:3.1.0'
		// FirebaseStorage.getInstance().getReference("photos").child(uid).child(template.ptsFolder);
	}

	private void onPtsPhotoRecieved(Uri uri)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, photoMaxSize));
		imageView.setPadding(30,30,30,30);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setAdjustViewBounds(true);
		imageView.setImageURI(uri);
		ptsPhotosContainer.addView(imageView);
	}

	@Override
	protected void onPartPhotoRecieved(Uri uri)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, photoMaxSize));
		imageView.setPadding(30,30,30,30);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setAdjustViewBounds(true);
		imageView.setImageURI(uri);
		partPhotosContainer.addView(imageView);
	}

	@Override
	protected void tryCreateRequest()
	{
		boolean valid = true;

		partContainer.setError(null);
		commentContainer.setError(null);

		if (ptsPhotos.isEmpty())
		{
			valid = false;
		}

		String part = partView.getText().toString();
		String comment = commentView.getText().toString();

		if (part.isEmpty())
		{
			partContainer.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}

		if (comment.length() > 500)
		{
			commentContainer.setError(getString(R.string.errorInvalidField));
			valid = false;
		}

		if (partPhotos.isEmpty())
		{
			Toast.makeText(getActivity(), getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			valid = false;
		}

		if (ptsPhotos.isEmpty())
		{
			Toast.makeText(getActivity(), getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			valid = false;
		}

		if (valid)
		{
			ArrayList<TitledUri> allPhotos = new ArrayList<>(ptsPhotos);
			allPhotos.addAll(partPhotos);
			listener.onSubmit(new Ticket(uid, ptsPhotosNames,
					Arrays.asList(part), comment, partPhotosNames), allPhotos);
		}
	}
}
