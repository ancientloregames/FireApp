package com.nimblemind.autoplus;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/21/2017.
 */

public class FirebaseUtils
{
	public static List snapshotToList(DataSnapshot dataSnapshot, Class<?> clazz)
	{
		List items = new ArrayList();
		Iterable<DataSnapshot> iter = dataSnapshot.getChildren();
		for (DataSnapshot item : iter)
		{
			Object obj = item.getValue();
			if (obj != null && obj.getClass() == clazz)
			{
				items.add(obj);
			}
		}
		return items;
	}
}
