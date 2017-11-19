package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import static android.app.Activity.RESULT_OK;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/6/2017.
 */

public class NewTicketTextFragment extends NewRequestFragment<Ticket>
{
	private static final int INTENT_AUTO_NAME = 101;
	private static final int INTENT_SPARE_PART = 102;

	private TextInputLayout nameContainer;
	private TextInputLayout vinContainer;
	private TextInputLayout yearContainer;
	private TextInputLayout partContainer;
	private TextInputLayout commentContainer;

	private ViewGroup partsListContainer;
	private ViewGroup photosListContainer;

	private TextView nameView;
	private TextView vinView;
	private TextView yearView;
	private TextView partView;
	private TextView commentView;

	protected Set<Uri> imageUris = new LinkedHashSet<>();

	public NewTicketTextFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (view != null)
		{
			nameContainer = view.findViewById(R.id.containerName);
			vinContainer = view.findViewById(R.id.containerVin);
			yearContainer = view.findViewById(R.id.containerYear);
			partContainer = view.findViewById(R.id.containerPart);
			commentContainer = view.findViewById(R.id.containerComment);

			partsListContainer = view.findViewById(R.id.containerPartsList);
			photosListContainer = view.findViewById(R.id.containerPhotosList);

			nameView = view.findViewById(R.id.textName);
			vinView = view.findViewById(R.id.textVin);
			yearView = view.findViewById(R.id.textYear);
			partView = view.findViewById(R.id.textPart);
			commentView = view.findViewById(R.id.textComment);

			nameView.setInputType(InputType.TYPE_NULL);
			partView.setInputType(InputType.TYPE_NULL);

			nameView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openList("AutoNames", INTENT_AUTO_NAME);
				}
			});

			partView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openList("parts", INTENT_SPARE_PART);
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
			if (requestCode == INTENT_ADD_PART_PHOTO)
			{
				Uri imageUri = data.getData();
				if (imageUri != null)
				{
					imageUris.add(imageUri);
					onPartPhotoRecieved(imageUri);
				}
			}
			else if (requestCode == INTENT_AUTO_NAME)
			{
				nameView.setText(data.getStringExtra("result"));
			}
			else if (requestCode == INTENT_SPARE_PART)
			{
				partView.setText(data.getStringExtra("result"));
			}
		}
	}

	@Override
	protected void populateWithTemplate(@NonNull Ticket template)
	{
		nameView.setText(template.autoName);
		nameView.setInputType(InputType.TYPE_NULL);
		nameView.setOnClickListener(null);
		vinView.setText(String.valueOf(template.vin));
		vinView.setInputType(InputType.TYPE_NULL);
		yearView.setText(String.valueOf(template.year));
		yearView.setInputType(InputType.TYPE_NULL);
		commentView.setText(template.comment);
		commentView.setInputType(InputType.TYPE_NULL);
		commentView.setFocusable(false);
		partView.setImeOptions(IME_ACTION_DONE);
	}

	@UiThread
	@Override
	protected void onPartPhotoRecieved(Uri uri)
	{
		ImageView imageView = new ImageView(getContext());
		imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
		imageView.setPadding(30,30,30,30);
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setAdjustViewBounds(true);
		imageView.setImageURI(uri);
		photosListContainer.addView(imageView);
	}

	@Override
	protected void tryCreateRequest()
	{
		boolean valid = true;

		String name = nameView.getText().toString();
		String vin = vinView.getText().toString();
		String part = partView.getText().toString();
		String comment = commentView.getText().toString();

		int year;
		try {
			year = Integer.valueOf(yearView.getText().toString());
		} catch(NumberFormatException | NullPointerException e) {
			yearContainer.setError(getString(R.string.errorInvalidField));
			return;
		}

		if (name.isEmpty())
		{
			nameContainer.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}

		if (vin.isEmpty())
		{
			vinContainer.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}
		else if (vin.length() != 17)
		{
			vinContainer.setError(getString(R.string.errorInvalidField));
			valid = false;
		}

		if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR))
		{
			yearContainer.setError(getString(R.string.errorInvalidField));
			valid = false;
		}

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

		Uri imageUri = imageUris.iterator().next();
		if (imageUri == null)
		{
			Toast.makeText(getActivity(), getString(R.string.errorPhotoRequered), Toast.LENGTH_SHORT).show();
			valid = false;
		}

		if (valid)
		{
			listener.onSubmit(new Ticket(uid, name, vin, year, comment, part, imageUri));
		}
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.new_ticket_text_fragment;
	}
}
