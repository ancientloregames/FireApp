package com.nimblemind.autoplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;


public class SupportProgressBar extends ProgressBar
{
	public SupportProgressBar(Context context)
	{
		super(context);
	}

	public SupportProgressBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SupportProgressBar, 0, 0);

		try
		{
			setColor(typedArray.getColor(R.styleable.SupportProgressBar_color, Color.BLACK));
		}
		finally
		{
			typedArray.recycle();
		}
	}

	public SupportProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public SupportProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setColor(final int color)
	{
		getIndeterminateDrawable().mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	}
}
