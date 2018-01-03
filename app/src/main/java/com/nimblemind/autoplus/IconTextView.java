package com.nimblemind.autoplus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class IconTextView extends AppCompatTextView
{
	public IconTextView(Context context)
	{
		super(context);
	}

	public IconTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		intialize(attrs);
	}

	public IconTextView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		intialize(attrs);
	}

	private void intialize(AttributeSet attrs)
	{
		int[] requireAttributes = new int[] { android.R.attr.textSize, android.R.attr.textColor };
		TypedArray typedArray = getContext().obtainStyledAttributes(attrs, requireAttributes);
		int textSize = typedArray.getDimensionPixelSize(0, -1);
		Drawable drawable = getCompoundDrawables()[0];
		typedArray.recycle();

		if (textSize != -1)
		{
			float whRatio = drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
			resizeDrawablesSize(new Rect(0, 0, Math.round(textSize * whRatio), textSize));
		}
	}

	private void resizeDrawablesSize(Rect bounds)
	{
		Drawable[] drawables = getCompoundDrawables();
		Drawable left = drawables[0];
		Drawable top = drawables[1];
		Drawable right = drawables[2];
		Drawable bottom = drawables[3];

		if (left != null)
		{
			left.setBounds(bounds);
		}

		if (top != null)
		{
			top.setBounds(bounds);
		}

		if (right != null)
		{
			right.setBounds(bounds);
		}

		if (bottom != null)
		{
			bottom.setBounds(bounds);
		}

		setCompoundDrawables(left, top, right, bottom);
	}
}
