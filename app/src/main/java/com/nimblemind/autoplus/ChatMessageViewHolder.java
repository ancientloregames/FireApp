package com.nimblemind.autoplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/28/2017.
 */

public class ChatMessageViewHolder extends RecyclerView.ViewHolder
{
	TextView infoView;
	TextView messageView;
	ImageView imageView;
	View imageContainer;

	private final static short MAX_IMAGE_DOWNLOAD_ATTEMPTS = 3;

	public ChatMessageViewHolder(View itemView)
	{
		super(itemView);

		infoView = itemView.findViewById(R.id.textInfo);
		messageView = itemView.findViewById(R.id.textMessage);
		imageView = itemView.findViewById(R.id.image);
		imageContainer = itemView.findViewById(R.id.imageContainer);
	}

	public void bindToData(ChatMessage message, StorageReference requestStorageRef)
	{
		infoView.setText(Utils.getDate(message.timestamp, "HH:mm"));

		if (!message.text.isEmpty())
		{
			messageView.setText(message.text);
			messageView.setVisibility(View.VISIBLE);
		}
		else messageView.setVisibility(View.GONE);

		if (message.images != null)
		{
			imageView.setImageURI(null);
			loadImage(requestStorageRef.child(message.images.get(0)), MAX_IMAGE_DOWNLOAD_ATTEMPTS);
			imageContainer.setVisibility(View.VISIBLE);
		}
		else imageContainer.setVisibility(View.GONE);
	}

	private void loadImage(final StorageReference reference, final int attempts)
	{
		final Context context = itemView.getContext();
		GlideApp.with(context)
				.asFile()
				.load(reference)
				.listener(new RequestListener<File>()
				{
					@Override
					public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource)
					{
						if (attempts > 0)
						{
							loadImage(reference, attempts - 1);
						}
						return true;
					}

					@Override
					public boolean onResourceReady(final File file, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource)
					{
						return false;
					}
				})
				.into(new SimpleTarget<File>()
				{
					@Override
					public void onResourceReady(final File file, Transition<? super File> transition)
					{
						Uri uri = Uri.fromFile(file);
						try
						{
							Bitmap bimap = Utils.decodeSampledBitmapFromUri(context.getContentResolver(), uri, 1536, 1536);
							imageView.setImageBitmap(bimap);
						}
						catch (IOException e)
						{
							Utils.trySendFabricReport("Failed to decode bitmap."
									+ "\nFile name: " + file.getName()
									+ "\nFile size: " + file.length(), e);
							imageView.setImageResource(android.R.drawable.ic_menu_report_image);
						}
						imageView.setImageURI(Uri.fromFile(file));
						imageView.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								Utils.openImage(context, file);
							}
						});
					}
				});
	}
}
