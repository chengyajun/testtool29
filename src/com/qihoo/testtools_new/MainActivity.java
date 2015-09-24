package com.qihoo.testtools_new;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo.testtools_new.view.ActionSheet;
import com.qihoo.testtools_new.view.SlidingMenu;

public class MainActivity extends FragmentActivity implements
		ActionSheet.ActionSheetListener {

	ImageButton top_banner_change_btn;
	ImageView buttom_banner_apps_btn, buttom_banner_history_btn,
			buttom_banner_settings_btn;
	LinearLayout buttom_banner_apps, buttom_banner_history,
			buttom_banner_settings;
	TextView buttom_banner_apps_tv, buttom_banner_history_tv,
			buttom_banner_settings_tv;
	TextView top_banner_title;
	FrameLayout fl_content;
	SlidingMenu mSlideMenu;

	FragmentApps mFragmentApps;
	FragmentHistory mFragmentHistory;
	FragmentSettings mFragmentSettings;

	FragmentManager mFragmentManager;
	FragmentTransaction mFragmentTransaction;
	MainBroadcastReceiver receiver;
	Button menu_advice_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initListener();

		mFragmentManager = getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.fl_content, mFragmentApps, "应用程序");
		mFragmentTransaction.commit();

		receiver = new MainBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.qihoo.testtool_new.setting_delete");
		registerReceiver(receiver, filter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initView() {
		top_banner_change_btn = (ImageButton) findViewById(R.id.top_banner_change_btn);
		buttom_banner_apps = (LinearLayout) findViewById(R.id.buttom_banner_apps);
		buttom_banner_history = (LinearLayout) findViewById(R.id.buttom_banner_history);
		buttom_banner_settings = (LinearLayout) findViewById(R.id.buttom_banner_settings);

		buttom_banner_apps_btn = (ImageView) findViewById(R.id.buttom_banner_apps_btn);
		buttom_banner_history_btn = (ImageView) findViewById(R.id.buttom_banner_history_btn);
		buttom_banner_settings_btn = (ImageView) findViewById(R.id.buttom_banner_settings_btn);

		buttom_banner_apps_tv = (TextView) findViewById(R.id.buttom_banner_apps_tv);
		buttom_banner_history_tv = (TextView) findViewById(R.id.buttom_banner_history_tv);
		buttom_banner_settings_tv = (TextView) findViewById(R.id.buttom_banner_settings_tv);

		top_banner_title = (TextView) findViewById(R.id.top_banner_title);
		fl_content = (FrameLayout) findViewById(R.id.fl_content);
		mSlideMenu = (SlidingMenu) findViewById(R.id.slidemenu);
		mFragmentApps = new FragmentApps();
		mFragmentHistory = new FragmentHistory();
		mFragmentSettings = new FragmentSettings();
		
		menu_advice_btn = (Button) findViewById(R.id.menu_advice_btn);

	}

	private void initListener() {

		top_banner_change_btn.setOnClickListener(listener);
		buttom_banner_apps.setOnClickListener(listener);
		buttom_banner_history.setOnClickListener(listener);
		buttom_banner_settings.setOnClickListener(listener);
		menu_advice_btn.setOnClickListener(listener);

	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.top_banner_change_btn: {// 点击切换按钮
				mSlideMenu.toggle();
			}
				break;
			case R.id.buttom_banner_apps: {// 点击应用程序

				mFragmentTransaction = mFragmentManager.beginTransaction();
				mFragmentTransaction.replace(R.id.fl_content, mFragmentApps,
						"应用程序");
				mFragmentTransaction.commit();
				// 恢复初始状态的界面显示
				clearAllBackground();
				// 设置点击按钮的图标变化
				buttom_banner_apps_btn
						.setImageResource(R.drawable.buttom_nav_app_a_07);
				buttom_banner_apps_tv.setTextColor(Color.parseColor("#55a8ea"));
				top_banner_title.setText("应用程序");

			}
				break;
			case R.id.buttom_banner_history: {// 点击历史记录
				mFragmentTransaction = mFragmentManager.beginTransaction();
				mFragmentTransaction.replace(R.id.fl_content, mFragmentHistory,
						"历史记录");
				mFragmentTransaction.commit();
				// 恢复初始状态的界面显示
				clearAllBackground();
				// 设置点击按钮的图标变化
				buttom_banner_history_btn
						.setImageResource(R.drawable.buttom_nav_history_a_07);
				buttom_banner_history_tv.setTextColor(Color
						.parseColor("#55a8ea"));
				top_banner_title.setText("历史记录");

			}
				break;
			case R.id.buttom_banner_settings: {// 点击设置
				mFragmentTransaction = mFragmentManager.beginTransaction();
				mFragmentTransaction.replace(R.id.fl_content,
						mFragmentSettings, "设置");
				mFragmentTransaction.commit();
				// 恢复初始状态的界面显示
				clearAllBackground();
				// 设置点击按钮的图标变化
				buttom_banner_settings_btn
						.setImageResource(R.drawable.buttom_nav_setting_a_07);
				buttom_banner_settings_tv.setTextColor(Color
						.parseColor("#55a8ea"));
				top_banner_title.setText("设置");
			}
				break;
			case R.id.menu_advice_btn:{//点击意见反馈
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AdviceActivity.class);
				startActivity(intent);
				
			}break;
			}

		}
	};

	private void clearAllBackground() {
		buttom_banner_apps_btn.setImageResource(R.drawable.buttom_nav_app_07);
		buttom_banner_history_btn
				.setImageResource(R.drawable.buttom_nav_history_07);
		buttom_banner_settings_btn
				.setImageResource(R.drawable.buttom_nav_setting_07);
		// main_my_info.setBackgroundResource(0);
		// main_friends_helper.setBackgroundResource(0);
		buttom_banner_apps_tv.setTextColor(Color.parseColor("#bcbcbc"));
		buttom_banner_history_tv.setTextColor(Color.parseColor("#bcbcbc"));
		buttom_banner_settings_tv.setTextColor(Color.parseColor("#bcbcbc"));

	}

	class MainBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.qihoo.testtool_new.setting_delete".equals(action)) {
				// 显示对话框

				setTheme(R.style.ActionSheetStyleiOS7);
				showActionSheet();

			}

		}

	}

	public void showActionSheet() {
		ActionSheet.createBuilder(this, getSupportFragmentManager())
				.setCancelButtonTitle("取消").setOtherButtonTitles("清除缓存")
				.setCancelableOnTouchOutside(true).setListener(this).show();
	}

	@Override
	public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

	}

	@Override
	public void onOtherButtonClick(ActionSheet actionSheet, int index) {

		File file1 = new File("/mnt/sdcard/testtool");
		if (!file1.exists() && !file1.isDirectory()) {
			System.out.println("//不存在");
			Toast.makeText(this, "无文件存在！", 3000).show();

		} else {

			System.out.println("//目录存在");
			deleteFile(file1);
			Toast.makeText(this, "测试记录删除成功", 3000).show();
			TestToolNewApplication.historyAppsList.clear();
			Intent intent = new Intent();
			intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST);
			sendBroadcast(intent);
		}

	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			Toast.makeText(this, "无文件存在！", 3000).show();
		}
	}

}
