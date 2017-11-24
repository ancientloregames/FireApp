package com.nimblemind.autoplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 11/1/2017.
 */

public class ListActivity extends AppCompatActivity
{
	private ListAdapter adapter;

	private DatabaseReference dbRef;

	private ScheduledFuture searchTask;

	private View progressBar;

	private final Runnable1<List<String>> dbResultCallback = new Runnable1<List<String>>()
	{
		@Override
		public void run(final List<String> list)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					progressBar.setVisibility(View.GONE);
					adapter.setItems(list);
				}
			});
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null)
		{
			onFirstCreate();
		}

		setContentView(R.layout.activity_list);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		progressBar = findViewById(R.id.progressBarContainer);

		RecyclerView recycler = findViewById(R.id.recycler);

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recycler.setLayoutManager(layoutManager);

		adapter = new ListAdapter(new ListAdapter.Listener()
		{
			@Override
			public void onItemSelected(String selected)
			{
				finishWithResult(selected);
			}
		});

		recycler.setAdapter(adapter);
	}

	@Override
	protected void onDestroy()
	{
		cancelCurrentSearchTask(true);
		adapter.clean();
		super.onDestroy();
	}

	private void onFirstCreate()
	{
		Intent intent = getIntent();
		if (intent != null)
		{
			String dbNodeName = intent.getStringExtra("dbNodeName");
			if (dbNodeName != null)
			{
				dbRef = FirebaseDatabase.getInstance().getReference().child(dbNodeName);
			}
			else throw new RuntimeException("You have to pass the dbNodeName to this activity!");
		}
		else throw new RuntimeException("You have to pass the dbNodeName to this activity!");
	}

	private void finishWithResult(String selected)
	{
		Intent intent = new Intent();
		intent.putExtra("result", selected);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_search, menu);

		MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
		searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		searchView.setFocusable(true);
		searchView.setIconified(false);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				if (!TextUtils.isEmpty(newText) && newText.length() > 2)
				{
					cancelCurrentSearchTask(false);
					searchTask = Executors.newSingleThreadScheduledExecutor().schedule(new Runnable()
					{
						@Override
						public void run()
						{
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									progressBar.setVisibility(View.VISIBLE);
								}
							});
							populateList(dbRef, newText, dbResultCallback);
						}
					}, 500, TimeUnit.MILLISECONDS);
				}
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void populateList(@NonNull DatabaseReference dbRef, final String filter, final Runnable1<List<String>> callback)
	{
		dbRef.orderByValue().startAt(filter).endAt(filter + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener()
		{
			@Override
			public void onDataChange(DataSnapshot dataSnapshot)
			{
				List<String> items = FirebaseUtils.snapshotToList(dataSnapshot, String.class);
				callback.run(items);
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				databaseError.toException().printStackTrace();
			}
		});
	}

	private boolean cancelCurrentSearchTask(boolean forced)
	{
		return searchTask != null && searchTask.cancel(forced);
	}

	static final class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
	{
		protected interface Listener
		{
			void onItemSelected(String selected);
		}

		private List<String> items = new ArrayList<>();

		private Listener listener;

		ListAdapter(Listener listener)
		{
			this.listener = listener;
		}

		void clean()
		{
			this.listener = null;
		}

		@UiThread
		void setItems(List<String> newItems)
		{
			items.clear();
			items.addAll(newItems);
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
		{
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position)
		{
			final String text = items.get(position);
			((TextView)holder.itemView).setText(text);

			holder.itemView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					listener.onItemSelected(text);
				}
			});
		}

		@Override
		public int getItemCount()
		{
			return items.size();
		}

		static final class ViewHolder extends RecyclerView.ViewHolder
		{
			ViewHolder(View itemView)
			{
				super(itemView);
			}
		}
	}
}
