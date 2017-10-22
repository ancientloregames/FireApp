package com.nimblemind.autoplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import static android.app.Activity.RESULT_OK;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/22/2017.
 */

public abstract class ClientRequestsFragment<MODEL extends Request, VIEWHOLDER extends RequestViewHolder>
		extends RequestsFragment<MODEL, VIEWHOLDER>
{
	public ClientRequestsFragment()
	{
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		view.findViewById(R.id.newRequestButton).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createNewRequest(null);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			if (requestCode == INTENT_NEW_REQUEST)
			{
				Request newRequest = (Request) data.getSerializableExtra("request");
				Uri photoUri = data.getParcelableExtra("photo");
				sendRequest(newRequest, photoUri);
			}
		}
	}

	protected void createNewRequest(@Nullable Request template)
	{
		Intent intent = new Intent(getActivity(), getNewRequestActivityClass());
		intent.putExtra("uid", uid);
		intent.putExtra("template", template);
		intent.putExtra("type", getModelClass().getSimpleName());
		startActivityForResult(intent, INTENT_NEW_REQUEST);
	}

	protected void sendRequest(Request request, Uri photoUri)
	{
		String key = database.push().getKey();
		database.child(key).setValue(request);

		getActivity().startService(new Intent(getActivity(), ImageUploadService.class)
				.putExtra(ImageUploadService.EXTRA_FILE_URI, photoUri)
				.putExtra(ImageUploadService.EXTRA_FILE_NAME, key)
				.putExtra(ImageUploadService.EXTRA_FILE_FOLDER, uid)
				.setAction(ImageUploadService.ACTION_UPLOAD));
	}

	protected void deleteRequest(final int position)
	{
		adapter.getRef(position).removeValue();
	}

	protected abstract Class<? extends NewRequestActivity> getNewRequestActivityClass();
}