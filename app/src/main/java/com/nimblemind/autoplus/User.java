package com.nimblemind.autoplus;

import java.io.Serializable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

class User implements Serializable
{
	final String name;
	final String email;

	public User() //For Firebase Database Snapshot
	{
		name = null;
		email = null;
	}

	User(String name, String email)
	{
		this.name = name;
		this.email = email;
	}

	@Override
	public String toString()
	{
		return "User: \n" +
				"name = '" + name + "\'\n" +
				"email = '" + email + '\'';
	}
}
