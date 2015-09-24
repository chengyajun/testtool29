package com.qihoo.testtools_new.view;

import com.qihoo.testtools_new.HistoryManagerDetailActivity;
import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.HistoryRecordThread;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class HistoryManagerSlidingMenu extends HorizontalScrollView {
	private LinearLayout mWapper;
	private ViewGroup mContent;
	private ViewGroup mRightDelete;
	private int mScreenWidth;

	private int mContentWidth;
	private int mRightDeleteWidth;
	// dp
	private int mMenuRightPadding = 80;

	private boolean once;

	private boolean isOpen;
	private Context mContext;

	HistoryBean bean;

	/**
	 * 未使用自定义属性时，调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public HistoryManagerSlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 当使用了自定义属性时，会调用此构造方法
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HistoryManagerSlidingMenu(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);
		this.mContext = context;
		if (!isInEditMode()) {
			// 获取我们定义的属性
			// TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
			// R.styleable.SlidingMenu, defStyle, 0);
			//
			// int n = a.getIndexCount();
			// for (int i = 0; i < n; i++) {
			// int attr = a.getIndex(i);
			// switch (attr) {
			// case R.styleable.SlidingMenu_rightPadding:
			// mMenuRightPadding = a.getDimensionPixelSize(attr,
			// (int) TypedValue
			// .applyDimension(
			// TypedValue.COMPLEX_UNIT_DIP, 100,
			// context.getResources()
			// .getDisplayMetrics()));
			// break;
			// }
			// }
			// a.recycle();

			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			mScreenWidth = outMetrics.widthPixels;

			mMenuRightPadding = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 80, context.getResources()
							.getDisplayMetrics());
		}
	}

	public HistoryManagerSlidingMenu(Context context) {
		this(context, null);
	}

	/**
	 * 设置子View的宽和高 设置自己的宽和高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!once) {

			mWapper = (LinearLayout) getChildAt(0);
			mContent = (ViewGroup) mWapper.getChildAt(0);// RelateLayout
															// history_manager_item_content

			mRightDelete = (ViewGroup) mWapper.getChildAt(1);
			mContentWidth = mContent.getLayoutParams().width = mScreenWidth;
			mRightDeleteWidth = mRightDelete.getLayoutParams().width = mMenuRightPadding;
			once = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 通过设置偏移量，将menu隐藏
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(0, 0);
		}
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent ev) {
	// int action = ev.getAction();
	//
	// switch (action) {
	// case MotionEvent.ACTION_UP:
	//
	// // 隐藏在左边的宽度
	// int scrollX = getScrollX();
	//
	// if (scrollX >= mRightDeleteWidth / 2) {
	// this.smoothScrollTo(mRightDeleteWidth, 0);
	// isOpen = false;
	// } else {
	// this.smoothScrollTo(0, 0);
	// isOpen = true;
	// }
	// return true;
	// }
	// return super.onTouchEvent(ev);
	// }

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			// 按下则先判断状态是否为显示，如果是显示，则关闭delete，如果是关闭则不处理
			if (isOpen) {
				this.smoothScrollTo(0, 0);

			}
			return true;
		}

		case MotionEvent.ACTION_UP: {
			// 判断如果是显示状态，则状态变为关闭，如果是关闭状态，则判断位移
			if (isOpen) {
				this.smoothScrollTo(0, 0);
				isOpen = false;

				return true;
			} else {
				// 隐藏在左边的宽度
				int scrollX = getScrollX();

				if (scrollX >= mRightDeleteWidth / 2) {
					this.smoothScrollTo(mRightDeleteWidth, 0);
					isOpen = true;
				} else if ((scrollX < mRightDeleteWidth / 2) && scrollX > 0) {
					this.smoothScrollTo(0, 0);
					isOpen = false;
				} else {
					this.smoothScrollTo(0, 0);
					isOpen = false;

					// Log.i("info","打开文件"+bean.getPackagename()+"_"+bean.getDate()+".csv");
					int position = (Integer) mContent.getTag();
					bean = TestToolNewApplication.historyDetailApplicationList
							.get(position);
					String fileName = bean.getPackagename() + "_"
							+ bean.getDate() + ".csv";
					// 启动线程
//					HistoryRecordThread historyRecordThread = new HistoryRecordThread(
//							fileName);
//					historyRecordThread.start();
					// 实现跳转
					Intent intent = new Intent();
					intent.setClass(mContext,
							HistoryManagerDetailActivity.class);
					intent.putExtra("appname", bean.getAppname());
					intent.putExtra("date", bean.getDate());
					intent.putExtra("filename", fileName);
					mContext.startActivity(intent);
				}
				return true;

			}
		}

		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 打开菜单
	 */
	public void openMenu() {
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}

	public void closeMenu() {
		if (!isOpen)
			return;
		this.smoothScrollTo(mRightDeleteWidth, 0);
		isOpen = false;
	}

	/**
	 * 切换菜单
	 */
	public void toggle() {
		if (isOpen) {
			closeMenu();
		} else {
			openMenu();
		}
	}

	/**
	 * 滚动发生时
	 */
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mRightDeleteWidth; // 1 ~ 0
		Log.i("info", "----------" + l);
		// ViewHelper.setTranslationX(mRightDelete, -(mRightDeleteWidth-l));

	}

}
