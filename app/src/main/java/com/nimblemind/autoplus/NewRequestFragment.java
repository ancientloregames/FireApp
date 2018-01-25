package com.nimblemind.autoplus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/14/2017.
 */

public abstract class NewRequestFragment<MODEL extends Request> extends Fragment
{
	interface Listener<MODEL extends Request>
	{
		void onSubmit(MODEL request, ArrayList<TitledUri> images);
	}

	protected static final int INTENT_ADD_PART_PHOTO = 100;
	protected static final int INTENT_SPARE_PART = 101;

	private static final String STATE_EXTRA_PART_IMAGES = "part_images";
	private static final String STATE_EXTRA_PART_IMAGES_NAMES = "part_images_names";

	protected Listener<MODEL> listener;

	protected String uid;

	protected final ArrayList<TitledUri> partPhotos = new ArrayList<>();

	protected final ArrayList<String> partPhotosNames = new ArrayList<>();

	protected int photoMaxSize;

	public NewRequestFragment()
	{
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		Bundle args = getArguments();
		if (args != null && args.containsKey(Constants.EXTRA_UID)) uid = args.getString(Constants.EXTRA_UID);
		else throw new RuntimeException("The Uid must be passed in order to create new request");

		if (context instanceof Listener) listener = (Listener) context;
		else throw new RuntimeException("Activity must be a listener of this fragment");

		photoMaxSize = context.getResources().getDimensionPixelSize(R.dimen.newRequestPhotoSize);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		return inflater.inflate(getLayoutId(), container, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == INTENT_ADD_PART_PHOTO && resultCode == RESULT_OK)
		{
			Uri imageUri = data.getData();
			if (imageUri != null)
			{
				addPartPhoto(imageUri);
			}
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle prevState)
	{
		super.onViewCreated(view, prevState);

		Bundle args = getArguments();
		if (args.containsKey(Constants.EXTRA_TEMPLATE))
		{
			String templateId = args.getParcelable(Constants.EXTRA_REQUEST_ID);
			MODEL template = args.getParcelable(Constants.EXTRA_TEMPLATE);
			populateWithTemplate(templateId, template);
		}

		if (prevState != null)
		{
			ArrayList<TitledUri> tmpPartPhotos = prevState.getParcelableArrayList(STATE_EXTRA_PART_IMAGES);
			partPhotos.addAll(tmpPartPhotos);
			partPhotosNames.addAll(prevState.getStringArrayList(STATE_EXTRA_PART_IMAGES_NAMES));
			for (TitledUri photo : partPhotos)
			{
				addPartPhotoView(photo.uri);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList(STATE_EXTRA_PART_IMAGES, partPhotos);
		outState.putStringArrayList(STATE_EXTRA_PART_IMAGES_NAMES,partPhotosNames);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy()
	{
		listener = null;
		super.onDestroy();
	}

	private void addPartPhoto(Uri uri)
	{
		String name = String.valueOf(System.currentTimeMillis());
		partPhotos.add(new TitledUri(name, uri));
		partPhotosNames.add(name);
		addPartPhotoView(uri);
	}

	protected void openList(String[] dbPath, int requestCode, int searchCount, int textFormat, String searchTarget)
	{
		Intent intent = new Intent(getActivity(), ListActivity.class);
		intent.putExtra(ListActivity.EXTRA_DB_PATH, dbPath);
		intent.putExtra(ListActivity.EXTRA_SEARCH_COUNT, searchCount);
		intent.putExtra(ListActivity.EXTRA_TEXT_FORMAT, textFormat);
		intent.putExtra(ListActivity.EXTRA_SEARCH_TARGET, searchTarget);
		startActivityForResult(intent, requestCode);
	}

	protected void openGallery(int intentCode)
	{
		Intent intent = new Intent(getActivity(), GalleryActivity.class);
		startActivityForResult(intent, intentCode);
	}

	@LayoutRes
	protected abstract int getLayoutId();

	protected abstract void populateWithTemplate(@NonNull String templateId, @NonNull MODEL template);

	protected abstract void addPartPhotoView(Uri uri);

	protected abstract void tryCreateRequest();
}
