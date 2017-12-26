package com.nimblemind.autoplus;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/29/2017.
 */

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>
{
	interface Listener
	{
		void onItemClick(File imageFile);
	}

	private final List<File> items = new ArrayList<>();
	private final Listener listener;

	GalleryAdapter(Listener listener)
	{
		this.listener = listener;
	}

	@UiThread
	void setItems(List<File> items)
	{
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false));
	}

	@Override
	public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position)
	{
		final File imageFile = items.get(position);

		holder.bindToData(imageFile);

		holder.itemView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				listener.onItemClick(imageFile);
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return items.size();
	}

	final class ViewHolder extends RecyclerView.ViewHolder
	{
		ViewHolder(View itemView)
		{
			super(itemView);
		}

		void bindToData(File imageFile)
		{
			Glide.with(itemView.getContext())
					.load(imageFile)
					.into((ImageView)itemView);
		}
	}
}
