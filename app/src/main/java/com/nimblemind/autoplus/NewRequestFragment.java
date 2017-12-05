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
		if (args != null && args.containsKey("uid")) uid = args.getString("uid");
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
		if (args.containsKey("template"))
		{
			populateWithTemplate((MODEL) args.getParcelable("template"));
		}

		if (prevState != null)
		{
			ArrayList<TitledUri> tmpPartPhotos = prevState.getParcelableArrayList("partPhotos");
			partPhotos.addAll(tmpPartPhotos);
			partPhotosNames.addAll(prevState.getStringArrayList("partPhotosNames"));
			for (TitledUri photo : partPhotos)
			{
				addPartPhotoView(photo.uri);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		outState.putParcelableArrayList("partPhotos", partPhotos);
		outState.putStringArrayList("partPhotosNames",partPhotosNames);
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

	protected abstract void populateWithTemplate(@NonNull MODEL template);

	protected abstract void addPartPhotoView(Uri uri);

	protected abstract void tryCreateRequest();
}
