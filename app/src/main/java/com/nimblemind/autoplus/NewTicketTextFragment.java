package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/6/2017.
 */

public class NewTicketTextFragment extends NewRequestFragment<Ticket>
{
	private static final int INTENT_AUTO_BRAND = 102;
	private static final int INTENT_AUTO_MODEL = 103;

	private TextInputLayout brandContainer;
	private TextInputLayout modelContainer;
	private TextInputLayout vinContainer;
	private TextInputLayout yearContainer;
	private TextInputLayout partContainer;
	private TextInputLayout commentContainer;

	private ViewGroup partsListContainer;
	private ViewGroup partPhotosContainer;

	private TextView brandView;
	private TextView modelView;
	private TextView vinView;
	private TextView yearView;
	private TextView partView;
	private TextView commentView;

	public NewTicketTextFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (view != null)
		{
			brandContainer = view.findViewById(R.id.containerBrand);
			modelContainer = view.findViewById(R.id.containerModel);
			vinContainer = view.findViewById(R.id.containerVin);
			yearContainer = view.findViewById(R.id.containerYear);
			partContainer = view.findViewById(R.id.containerPart);
			commentContainer = view.findViewById(R.id.containerComment);

			partsListContainer = view.findViewById(R.id.containerPartsList);
			partPhotosContainer = view.findViewById(R.id.containerPhotosList);

			brandView = view.findViewById(R.id.textBrand);
			modelView = view.findViewById(R.id.textModel);
			vinView = view.findViewById(R.id.textVin);
			yearView = view.findViewById(R.id.textYear);
			partView = view.findViewById(R.id.textPart);
			commentView = view.findViewById(R.id.textComment);

			brandView.setInputType(InputType.TYPE_NULL);
			modelView.setInputType(InputType.TYPE_NULL);
			partView.setInputType(InputType.TYPE_NULL);

			brandView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					modelView.setText("");
					openList(new String[]{ "autoBrands" }, INTENT_AUTO_BRAND, 2,
							Utils.FORMAT_UPPER_CASE, getString(R.string.textSearchBrand));
				}
			});

			modelView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String brand = brandView.getText().toString();
					if (!brand.isEmpty())
					{
						openList(new String[]{ "autoModels", brand }, INTENT_AUTO_MODEL, 1,
								Utils.FORMAT_LOWER_CASE, getString(R.string.textSearchModel));
					}
				}
			});

			partView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openList(new String[]{ "parts" }, INTENT_SPARE_PART, 3,
							Utils.FORMAT_CAP_SENTENCE, getString(R.string.textSearchPart));
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

		if (resultCode == RESULT_OK && data != null)
		{
			String result = data.getStringExtra("result");
			if (result != null)
			{
				switch (requestCode)
				{
					case INTENT_AUTO_BRAND:
						brandView.setText(result);
						break;
					case INTENT_AUTO_MODEL:
						modelView.setText(result);
						break;
					case INTENT_SPARE_PART:
						partView.setText(result);
						break;
				}
			}
		}
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.new_ticket_text_fragment;
	}

	@Override
	protected void populateWithTemplate(@NonNull Ticket template)
	{
		brandView.setText(template.autoBrand);
		brandView.setInputType(InputType.TYPE_NULL);
		brandView.setOnClickListener(null);
		modelView.setText(template.autoModel);
		modelView.setInputType(InputType.TYPE_NULL);
		modelView.setOnClickListener(null);
		vinView.setText(String.valueOf(template.vin));
		vinView.setInputType(InputType.TYPE_NULL);
		yearView.setText(String.valueOf(template.year));
		yearView.setInputType(InputType.TYPE_NULL);
		partView.setImeOptions(IME_ACTION_DONE);
	}

	@Override
	protected void addPartPhotoView(Uri uri)
	{
		final ImageView imageView = (ImageView) getLayoutInflater()
				.inflate(R.layout.horizontal_gallery_item, partPhotosContainer,false);
		imageView.setImageURI(uri);
		partPhotosContainer.addView(imageView);
	}

	@Override
	protected void tryCreateRequest()
	{
		boolean valid = true;

		yearContainer.setError(null);
		brandContainer.setError(null);
		modelContainer.setError(null);
		vinContainer.setError(null);
		partContainer.setError(null);
		commentContainer.setError(null);

		String brand = brandView.getText().toString();
		String model = modelView.getText().toString();
		String vin = vinView.getText().toString();
		String part = partView.getText().toString();
		String comment = commentView.getText().toString();

		int year;
		try {
			year = Integer.valueOf(yearView.getText().toString());
		} catch(NumberFormatException | NullPointerException e) {
			year = 0;
		}

		if (brand.isEmpty())
		{
			brandContainer.setError(getString(R.string.errorFieldRequired));
			valid = false;
		}

		if (model.isEmpty())
		{
			modelContainer.setError(getString(R.string.errorFieldRequired));
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
			listener.onSubmit(new Ticket(uid, brand, model, vin, year,
					Arrays.asList(part), comment, partPhotosNames), partPhotos);
		}
	}
}
