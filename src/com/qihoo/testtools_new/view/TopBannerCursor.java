package com.qihoo.testtools_new.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TopBannerCursor extends ImageView {
public TopBannerCursor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public TopBannerCursor(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	private int width;
	private int height;
	public TopBannerCursor(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}

}
