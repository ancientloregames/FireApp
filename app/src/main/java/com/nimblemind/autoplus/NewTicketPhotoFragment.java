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
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/19/2017.
 */

public class NewTicketPhotoFragment extends NewRequestFragment<Ticket>
{
	protected static final int INTENT_ADD_PTS_PHOTO = 102;

	private static final String STATE_EXTRA_PTS_IMAGES = "pts_images";
	private static final String STATE_EXTRA_PTS_IMAGES_NAMES = "pts_images_names";

	private TextInputLayout partContainer;
	private TextInputLayout commentContainer;

	private ViewGroup partPhotosContainer;
	private ViewGroup ptsPhotosContainer;

	private View ptsPhotoButton;

	private TextView partView;
	private TextView commentView;

	protected final ArrayList<TitledUri> ptsPhotos = new ArrayList<>();

	protected final ArrayList<String> ptsPhotosNames = new ArrayList<>();

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
					openList(new String[] { Constants.DB_REF_PARTS }, INTENT_SPARE_PART, 3,
							Utils.FORMAT_CAP_SENTENCE, getString(R.string.textSearchPart));
				}
			});

			ptsPhotoButton = view.findViewById(R.id.buttonAddPTSPhoto);
			ptsPhotoButton.setOnClickListener(new View.OnClickListener()
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
	public void onViewCreated(View view, @Nullable Bundle prevState)
	{
		super.onViewCreated(view, prevState);

		if (prevState != null)
		{
			ArrayList<TitledUri> tmpPtsPhotos = prevState.getParcelableArrayList(STATE_EXTRA_PTS_IMAGES);
			ptsPhotos.addAll(tmpPtsPhotos);
			ptsPhotosNames.addAll(prevState.getStringArrayList(STATE_EXTRA_PTS_IMAGES_NAMES));
			for (TitledUri photo : ptsPhotos)
			{
				addPtsPhotoView(photo.uri);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList(STATE_EXTRA_PTS_IMAGES, ptsPhotos);
		outState.putStringArrayList(STATE_EXTRA_PTS_IMAGES_NAMES, ptsPhotosNames);
		super.onSaveInstanceState(outState);
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
					addPtsPhoto(imageUri);
				}
			}
			else if (requestCode == INTENT_SPARE_PART)
			{
				partView.setText(data.getStringExtra(Constants.EXTRA_RESULT));
			}
		}
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.new_ticket_photo_fragment;
	}

	@Override
	protected void populateWithTemplate(@NonNull String templateId, @NonNull Ticket template)
	{
		ptsPhotoButton.setOnClickListener(null);
		StorageReference requestStorageRef = FirebaseStorage.getInstance()
				.getReference(Constants.DB_REF_PHOTOS).child(templateId);
		for (String image : template.ptsPhotos)
		{
			GlideApp.with(this)
					.asFile()
					.load(requestStorageRef.child(image))
					.into(new SimpleTarget<File>()
					{
						@Override
						public void onResourceReady(File file, Transition<? super File> transition)
						{
							addPtsPhoto(Uri.fromFile(file));
						}
					});
		}
	}

	private void addPtsPhoto(Uri uri)
	{
		String name = String.valueOf(System.currentTimeMillis());
		ptsPhotos.add(new TitledUri(name, uri));
		ptsPhotosNames.add(name);
		addPtsPhotoView(uri);
	}

	private void addPtsPhotoView(Uri uri)
	{
		final View imageLayout = getLayoutInflater()
				.inflate(R.layout.horizontal_gallery_item, ptsPhotosContainer, false);
		final ImageView imageView = imageLayout.findViewById(R.id.image);
		imageView.setImageURI(uri);
		ptsPhotosContainer.addView(imageLayout);
	}

	@Override
	protected void addPartPhotoView(Uri uri)
	{
		final View imageLayout = getLayoutInflater()
				.inflate(R.layout.horizontal_gallery_item, partPhotosContainer, false);
		final ImageView imageView = imageLayout.findViewById(R.id.image);
		imageView.setImageURI(uri);
		partPhotosContainer.addView(imageLayout);
	}

	@Override
	protected void tryCreateRequest()
	{
		boolean valid = true;

		partContainer.setError(null);
		commentContainer.setError(null);

		String part = partView.getText().toString();
		String comment = commentView.getText().toString();

		if (ptsPhotos.isEmpty())
		{
			Toast.makeText(getActivity(), getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			valid = false;
		}

		if (part.isEmpty() && partPhotosContainer.getChildCount() < 1)
		{
			partContainer.setError(getString(R.string.errorTextOrPhoto));
			valid = false;
		}

		if (comment.length() > 500)
		{
			commentContainer.setError(getString(R.string.errorInvalidField));
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
