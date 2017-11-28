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
	public final static String EXTRA_DB_PATH = "extra_db_path";
	public final static String EXTRA_SEARCH_COUNT = "extra_search_count";

	private ListAdapter adapter;

	private DatabaseReference dbRef;

	private ScheduledFuture searchTask;

	private View progressBar;

	private View emptyResultView;

	private int searchCount;

	private String searchText;

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
					emptyResultView.setVisibility(View.GONE);
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

		Intent intent = getIntent();
		String[] dbPath = intent.getStringArrayExtra(EXTRA_DB_PATH);
		if (dbPath != null)
		{
			dbRef = FirebaseDatabase.getInstance().getReference();
			for (String segment : dbPath)
			{
				dbRef = dbRef.child(segment);
			}
			searchCount = intent.getIntExtra(EXTRA_SEARCH_COUNT, 3);
		}
		else throw new RuntimeException("You have to pass the dbPath to this activity!");

		setContentView(R.layout.activity_list);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		progressBar = findViewById(R.id.progressBarContainer);
		emptyResultView = findViewById(R.id.addButtonContainer);
		emptyResultView.findViewById(R.id.buttonAdd).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finishWithResult(searchText);
			}
		});

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
				if (!TextUtils.isEmpty(newText) && newText.length() >= searchCount)
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
							searchText = newText.toUpperCase();
							populateList(dbRef, searchText, dbResultCallback);
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
				if (!items.isEmpty())
				{
					callback.run(items);
				}
				else runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						emptyResultView.setVisibility(View.VISIBLE);
						progressBar.setVisibility(View.GONE);
					}
				});
			}

			@Override
			public void onCancelled(DatabaseError databaseError)
			{
				Utils.trySendFabricReport("ListActivity.populateList(): filter = " + filter, databaseError.toException());
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
			return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_activity_item, parent, false));
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position)
		{
			final String text = items.get(position);
			holder.textView.setText(text);

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
			TextView textView;

			ViewHolder(View itemView)
			{
				super(itemView);

				textView = itemView.findViewById(R.id.text);
			}
		}
	}
}
