package com.qihoo.testtools_new.adapter;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo.testtools_new.HistoryManagerActivity;
import com.qihoo.testtools_new.R;
import com.qihoo.testtools_new.TestToolNewApplication;
import com.qihoo.testtools_new.bean.ApplicationBean1;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.HistoryApplicationThread;
import com.qihoo.testtools_new.thread.ListHistoryAppsThread;
import com.qihoo.testtools_new.view.HistoryManagerSlidingMenu;

public class HistoryManagerAdapter extends BaseAdapter {

	private Context mContext;
	private int mResId;
	private LayoutInflater inflater;
	private ArrayList<HistoryBean> historyDetailList;

	// 构造方法
	public HistoryManagerAdapter(Context context, int resId, ArrayList<HistoryBean> list) {
		this.mContext = context;
		this.mResId = resId;
		this.inflater = LayoutInflater.from(context);
		setdata(list);
	}

	public void setdata(List<HistoryBean> list) {
		if (list == null) {
			list = new ArrayList<HistoryBean>();
		}
		this.historyDetailList = (ArrayList<HistoryBean>) list;

	}

	public void setDataChanged(List<HistoryBean> historyAppsList) {
		setdata(historyAppsList);
	}

	@Override
	public int getCount() {
		return historyDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		return historyDetailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 可重用视图
		if (convertView == null) {
			convertView = inflater.inflate(mResId, null);
		}
		// 获取当前值
		final HistoryBean bean = historyDetailList.get(position);

		// 时间
		TextView tvAppData = (TextView) convertView.findViewById(R.id.tv_history_manager_data);
		// tvAppName.setText("" + bean);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(bean.getDate());
		String date = format.format(d);
		tvAppData.setText("" + date);

		// 编号
		TextView tv_history_manager_num = (TextView) convertView.findViewById(R.id.tv_history_manager_num);
		tv_history_manager_num.setText("" + (position + 1) + ".");

		RelativeLayout contentLayout = (RelativeLayout) convertView.findViewById(R.id.history_manager_item_content);
		contentLayout.setTag(position);

		CheckBox choose = (CheckBox) convertView.findViewById(R.id.history_manager_item_cb);

		choose.setChecked(bean.isChoose);

		// if (!bean.isChoose) {
		//
		// for (int i = 0; i < TestToolNewApplication.historyRecodeDataList
		// .size(); i++) {
		//
		// if (TestToolNewApplication.historyRecodeDataList.get(i)
		// .getDate() == bean.getDate()) {
		// TestToolNewApplication.historyRecodeDataList.remove(i);
		// }
		// }
		//
		// } else {
		// boolean isExist = false;
		// for (int i = 0; i < TestToolNewApplication.historyRecodeDataList
		// .size(); i++) {
		//
		// if (TestToolNewApplication.historyRecodeDataList.get(i)
		// .getDate() == bean.getDate()) {
		// isExist = true;
		// }
		// }
		//
		// if(!isExist){
		// TestToolNewApplication.historyRecodeDataList.add(bean);
		// }
		//
		//
		// }

		choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bean.isChoose) {
					bean.isChoose = false;
					((CheckBox) v).setChecked(false);

					// for (int i = 0; i <
					// TestToolNewApplication.historyRecodeDataList
					// .size(); i++) {
					//
					// if (TestToolNewApplication.historyRecodeDataList.get(i)
					// .getDate() == bean.getDate()) {
					// TestToolNewApplication.historyRecodeDataList
					// .remove(i);
					// }
					// }

					// bean.isChoose = isChecked;
					// 状态改变则发广播更改界面
					Intent intent = new Intent();
					intent.setAction("com.qihoo.testtool.new.history.manager.broadcastreceiver1");
					intent.putExtra("isChoose", false);
					mContext.sendBroadcast(intent);

				} else {
					bean.isChoose = true;

					// TestToolNewApplication.historyRecodeDataList.add(bean);

					// bean.isChoose = isChecked;
					// 状态改变则发广播更改界面
					Intent intent = new Intent();
					intent.setAction("com.qihoo.testtool.new.history.manager.broadcastreceiver1");
					intent.putExtra("isChoose", true);
					mContext.sendBroadcast(intent);
				}

			}
		});

		final com.qihoo.testtools_new.view.HistoryManagerSlidingMenu history_manager_slidemenu = (HistoryManagerSlidingMenu) convertView
				.findViewById(R.id.history_manager_slidemenu);
		// 删除操作
		ImageButton historyDelete = (ImageButton) convertView.findViewById(R.id.history_manager_delete);
		historyDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String fileName = bean.getPackagename() + "_" + bean.getDate() + ".csv";
				File file = new File("/mnt/sdcard/testtool", fileName);
				if (file.exists()) { // 判断文件是否存在
					if (file.isFile()) { // 判断是否是文件
						boolean isDelete = file.delete(); // delete()方法 你应该知道
															// 是删除的意思;
						if (isDelete) {
							Toast.makeText(mContext, "删除成功", 3000).show();
							history_manager_slidemenu.smoothScrollTo(0, 0);
							// 启动线程，加载该app的所有测试记录
							// HistoryApplicationThread historyAppThread = new
							// HistoryApplicationThread(
							// bean.getPackagename(), bean.getAppname());
							// historyAppThread.start();

							// 修改数据
							// TestToolNewApplication.historyDetailApplicationList
							for (int i = 0; i < TestToolNewApplication.historyDetailApplicationList.size(); i++) {
								if (TestToolNewApplication.historyDetailApplicationList.get(i).getDate() == bean
										.getDate()) {
									TestToolNewApplication.historyDetailApplicationList.remove(i);
								}
							}

							// 更新历史记录首页的数据

							ListHistoryAppsThread historyAppsThread = new ListHistoryAppsThread();
							historyAppsThread.start();

							// 发送广播更新数据和界面
							Intent intent = new Intent();
							intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST);
							TestToolNewApplication.mySelf.sendStickyBroadcast(intent);

						}
					}
				}

			}
		});
		// //实现跳转
		// RelativeLayout history_manager_item_content = (RelativeLayout)
		// convertView
		// .findViewById(R.id.history_manager_item_content);
		// history_manager_item_content.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent();
		// intent.setClass(mContext, HistoryManagerDetailActivity.class);
		// mContext.startActivity(intent);
		// }
		// });

		// // 点击跳转到测试管理页面
		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Log.i("info", "===========点击跳转==========");
		// Intent intent = new Intent();
		// intent.setClass(mContext, HistoryManagerDetailActivity.class);
		// mContext.startActivity(intent);
		// }
		// });
		return convertView;
	}

}
