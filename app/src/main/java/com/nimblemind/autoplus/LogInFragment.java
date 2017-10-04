package com.nimblemind.autoplus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * com.nimblemind.autoplus. Created by nimblemind on 9/27/2017.
 */


public class LogInFragment extends Fragment
{
	private Listener mListener;

	private EditText mEmailView;
	private EditText mPasswordView;

	public LogInFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		mEmailView = view.findViewById(R.id.textEmail);
		mPasswordView = view.findViewById(R.id.textPassword);

		view.findViewById(R.id.buttonLogin)
				.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String email = mEmailView.getText().toString();
						String password = mPasswordView.getText().toString();

						// TODO Сдеать проверку корректности введенных данных
						mListener.onLogIn(email, password);
					}
				});

		view.findViewById(R.id.buttonGotoSignup).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mListener.onGotoSignUp();
			}
		});
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		if (context instanceof Listener)
		{
			mListener = (Listener) context;
		}
		else
		{
			throw new RuntimeException(context.toString()
					+ " must implement " + this.getClass().getSimpleName() + ".Listener");
		}

		getActivity().setTitle(R.string.fragmentLoginName);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	interface Listener
	{
		void onLogIn(String email, String password);
		void onGotoSignUp();
	}
}
