package com.nimblemind.autoplus;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.Nullable;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseIndexArray;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 1/13/2018.
 */

public class FirebaseRecyclerOptions<T>
{
	private final ObservableSnapshotArray<T> mSnapshots;
	private final LifecycleOwner mOwner;

	private FirebaseRecyclerOptions(ObservableSnapshotArray<T> snapshots, @Nullable LifecycleOwner owner)
	{
		mSnapshots = snapshots;
		mOwner = owner;
	}

	/**
	 * Get the {@link ObservableSnapshotArray} to listen to.
	 */
	public ObservableSnapshotArray<T> getSnapshots()
	{
		return mSnapshots;
	}

	/**
	 * Get the (optional) LifecycleOwner. Listening will start/stop after the appropriate lifecycle
	 * events.
	 */
	@Nullable
	public LifecycleOwner getOwner()
	{
		return mOwner;
	}

	/**
	 * Builder for a {@link FirebaseRecyclerOptions}.
	 *
	 * @param <T> the model class for the {@link FirebaseRecyclerAdapter}.
	 */
	public static class Builder<T>
	{
		private ObservableSnapshotArray<T> mSnapshots;
		private LifecycleOwner mOwner;

		/**
		 * Directly set the {@link ObservableSnapshotArray} to be listened to.
		 * <p>
		 * Do not call this method after calling {@code setQuery}.
		 */
		public Builder<T> setSnapshotArray(ObservableSnapshotArray<T> snapshots)
		{
			mSnapshots = snapshots;
			return this;
		}

		/**
		 * Set the Firebase query to listen to, along with a {@link SnapshotParser} to parse
		 * snapshots into model objects.
		 * <p>
		 * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
		 */
		public Builder<T> setQuery(Query query, SnapshotParser<T> snapshotParser)
		{
			mSnapshots = new FirebaseSortableArray<>(query, snapshotParser);
			return this;
		}

		/**
		 * Set the Firebase query to listen to, along with a {@link Class} to which snapshots should
		 * be parsed.
		 * <p>
		 * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
		 */
		public Builder<T> setQuery(Query query, Class<T> modelClass)
		{
			return setQuery(query, new ClassSnapshotParser<>(modelClass));
		}

		/**
		 * Set an indexed Firebase query to listen to, along with a {@link SnapshotParser} to parse
		 * snapshots into model objects. Keys are identified by the {@code keyQuery} and then data
		 * is fetched using those keys from the {@code dataRef}.
		 * <p>
		 * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
		 */
		public Builder<T> setIndexedQuery(Query keyQuery, DatabaseReference dataRef, SnapshotParser<T> snapshotParser)
		{
			mSnapshots = new FirebaseIndexArray<>(keyQuery, dataRef, snapshotParser);
			return this;
		}

		/**
		 * Set an indexed Firebase query to listen to, along with a {@link Class} to which snapshots
		 * should be parsed. Keys are identified by the {@code keyQuery} and then data is fetched
		 * using those keys from the {@code dataRef}.
		 * <p>
		 * Do not call this method after calling {@link #setSnapshotArray(ObservableSnapshotArray)}.
		 */
		public Builder<T> setIndexedQuery(Query keyQuery, DatabaseReference dataRef, Class<T> modelClass)
		{
			return setIndexedQuery(keyQuery, dataRef, new ClassSnapshotParser<>(modelClass));
		}

		/**
		 * Set the (optional) {@link LifecycleOwner}. Listens will start and stop after the
		 * appropriate lifecycle events.
		 */
		public Builder<T> setLifecycleOwner(LifecycleOwner owner)
		{
			mOwner = owner;
			return this;
		}

		/**
		 * Build a {@link FirebaseRecyclerOptions} from the provided arguments.
		 */
		public FirebaseRecyclerOptions<T> build()
		{
			return new FirebaseRecyclerOptions<>(mSnapshots, mOwner);
		}
	}
}
