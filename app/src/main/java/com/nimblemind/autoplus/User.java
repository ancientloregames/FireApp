package com.nimblemind.autoplus;

import android.support.annotation.NonNull;

import java.io.Serializable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

class User implements Serializable
{// fields must be public for firebase database
	public final String name;
	public final String email;
	public final UserType type;
	public final boolean verified;

	public User() //For Firebase Database Snapshot
	{
		name = null;
		email = null;
		type = UserType.CLIENT;
		verified = false;
	}

	User(@NonNull final String name, @NonNull final String email) // Default Account type on sign up
	{
		this.name = name;
		this.email = email;
		this.type = UserType.CLIENT;
		verified = false;
	}

	User(@NonNull final String name, @NonNull final String email, @NonNull final UserType type)
	{
		this.name = name;
		this.email = email;
		this.type = type;
		verified = false;
	}
}
