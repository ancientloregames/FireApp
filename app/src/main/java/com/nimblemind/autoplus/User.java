package com.nimblemind.autoplus;

import java.io.Serializable;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 10/4/2017.
 */

class User implements Serializable
{
	final String id;
	final String name;
	final String email;

	User(String id, String name, String email)
	{
		this.id = id;
		this.name = name;
		this.email = email;
	}

	@Override
	public String toString()
	{
		return "User{ " +
				"id = '" + id + '\'' +
				", name = '" + name + '\'' +
				", email = '" + email + '\'' +
				" }";
	}
}
