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

public class SignUpFragment extends Fragment
{
	private Listener mListener;

	private EditText mEmailView;
	private EditText mNameView;
	private EditText mPasswordView;
	private EditText mPassConfirmView;

	public SignUpFragment()
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_signup, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		mEmailView = view.findViewById(R.id.textEmail);
		mNameView = view.findViewById(R.id.textName);
		mPasswordView = view.findViewById(R.id.textPassword);
		mPassConfirmView = view.findViewById(R.id.textPassword2);

		view.findViewById(R.id.buttonSignup)
				.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						String email = mEmailView.getText().toString();
						String name = mNameView.getText().toString();
						String password = mPasswordView.getText().toString();
						String confirm = mPassConfirmView.getText().toString();

						if (validate(email, name, password, confirm))
						{
							mListener.onSignUp(email, name, password);
						}
					}
				});

		view.findViewById(R.id.buttonGotoLogin).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mListener.onGotoLogIn();
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

		getActivity().setTitle(R.string.fragmentSignupName);
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mListener = null;
	}

	private boolean validate(String email, String name, String password, String confirm)
	{
		boolean result = true;

		mEmailView.setError(null);
		mNameView.setError(null);
		mPasswordView.setError(null);

		if (email.isEmpty())
		{
			mEmailView.setError(getString(R.string.errorFieldRequired));
			result = false;
		}
		else if (!email.matches(".+@.+\\..+"))
		{
			mEmailView.setError(getString(R.string.errorInvalidEmail));
			result = false;
		}

		if (name.isEmpty())
		{
			mNameView.setError(getString(R.string.errorFieldRequired));
			result = false;
		}

		if (password.isEmpty())
		{
			mPasswordView.setError(getString(R.string.errorFieldRequired));
			result = false;
		}
		else if (password.length() < 6)
		{
			mPasswordView.setError(getString(R.string.errorInvalidPassword));
			result = false;
		}

		if (!password.equals(confirm))
		{
			mPassConfirmView.setError(getString(R.string.errorConfirmPassFailure));
			result = false;
		}

		return result;
	}

	interface Listener
	{
		void onSignUp(String email, String name, String password);
		void onGotoLogIn();
	}
}
