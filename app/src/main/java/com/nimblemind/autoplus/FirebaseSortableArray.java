package com.nimblemind.autoplus;

import android.util.Log;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 1/13/2018.
 */

public class FirebaseSortableArray<T> extends FirebaseArray<T>
{
	//private SortParam sortParam = new SortParam(Constants.DB_REF_UNREAD_CLIENT_MESSAGES, "desc");

	int mode;

	public FirebaseSortableArray(Query query, SnapshotParser<T> parser)
	{
		super(query, parser);
	}

	@Override
	public void onChildAdded(DataSnapshot snapshot, String previousChildKey)
	{
		List<DataSnapshot> list = getSnapshots();

		int index = 0;
		if (mode == Constants.QUERY_MODE_UNANSWERED)
		{
			String paramName = Constants.DB_REF_UNREAD_CLIENT_MESSAGES;
			int param = snapshot.child(paramName).getValue(int.class);
			Log.d("firebaseArray", "Addable: " + param);
			for (index = 0; index < list.size(); index++)
			{
				int comparable = list.get(index).child(paramName).getValue(int.class);
				Log.d("firebaseArray", "comparable: " + comparable);
				if (param <= comparable)
				{
					Log.d("firebaseArray", "inserted at: " + index);
					break;
				}
			}
		}
		else if (previousChildKey != null)
		{
			index = getIndex(previousChildKey) + 1;
		}

//		int index = 0;
//		if (sortParam != null)
//		{
//			int size = list.size();
//			int param = snapshot.child(sortParam.name).getValue(int.class);
//			for (int i = 0; i < size; i++)
//			{
//				int comparable = snapshot.child(sortParam.name).getValue(int.class);
//				if (compare(param, comparable, sortParam.direction))
//				{
//					index = i == 0 ? i : i - 1;
//					break;
//				}
//			}
//		}
//		else if (previousChildKey != null)
//		{
//			index = getIndex(previousChildKey) + 1;
//		}

		list.add(index, snapshot);
		notifyOnChildChanged(ChangeEventType.ADDED, snapshot, index, -1);
	}

	private boolean compare(int val1, int val2, String direction)
	{
		return direction.equals("asc")
				? val1 >= val2
				: val1 <= val2;
	}

	private int getIndex(String key)
	{
		int index = 0;
		for (DataSnapshot snapshot : getSnapshots())
		{
			if (snapshot.getKey().equals(key))
			{
				return index;
			}
			else
			{
				index++;
			}
		}
		throw new IllegalArgumentException("Key not found");
	}

	static final class SortParam
	{
		String name;
		String direction = "asc";

		public SortParam(String name, String direction)
		{
			this.name = name;
			this.direction = direction;
		}
	}
}
