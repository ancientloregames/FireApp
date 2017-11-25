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

	protected final int maxPartPhotoCount = 1;

	protected final ArrayList<TitledUri> partPhotos = new ArrayList<>(maxPartPhotoCount);

	protected final ArrayList<String> partPhotosNames = new ArrayList<>(maxPartPhotoCount);

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
				String name = String.valueOf(System.currentTimeMillis());
				partPhotos.add(new TitledUri(name, imageUri));
				partPhotosNames.add(name);
				onPartPhotoRecieved(imageUri);
			}
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		Bundle args = getArguments();
		if (args.containsKey("template"))
		{
			populateWithTemplate((MODEL) args.getParcelable("template"));
		}
	}

	@Override
	public void onDestroy()
	{
		listener = null;
		super.onDestroy();
	}

	protected void openList(String[] dbPath, int requestCode, int searchCount)
	{
		Intent intent = new Intent(getActivity(), ListActivity.class);
		intent.putExtra(ListActivity.EXTRA_DB_PATH, dbPath);
		intent.putExtra(ListActivity.EXTRA_SEARCH_COUNT, searchCount);
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

	protected abstract void onPartPhotoRecieved(Uri uri);

	protected abstract void tryCreateRequest();
}
