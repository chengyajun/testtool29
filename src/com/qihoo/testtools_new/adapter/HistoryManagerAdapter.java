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

	// ���췽��
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
		// ��������ͼ
		if (convertView == null) {
			convertView = inflater.inflate(mResId, null);
		}
		// ��ȡ��ǰֵ
		final HistoryBean bean = historyDetailList.get(position);

		// ʱ��
		TextView tvAppData = (TextView) convertView.findViewById(R.id.tv_history_manager_data);
		// tvAppName.setText("" + bean);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date(bean.getDate());
		String date = format.format(d);
		tvAppData.setText("" + date);

		// ���
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
					// ״̬�ı��򷢹㲥���Ľ���
					Intent intent = new Intent();
					intent.setAction("com.qihoo.testtool.new.history.manager.broadcastreceiver1");
					intent.putExtra("isChoose", false);
					mContext.sendBroadcast(intent);

				} else {
					bean.isChoose = true;

					// TestToolNewApplication.historyRecodeDataList.add(bean);

					// bean.isChoose = isChecked;
					// ״̬�ı��򷢹㲥���Ľ���
					Intent intent = new Intent();
					intent.setAction("com.qihoo.testtool.new.history.manager.broadcastreceiver1");
					intent.putExtra("isChoose", true);
					mContext.sendBroadcast(intent);
				}

			}
		});

		final com.qihoo.testtools_new.view.HistoryManagerSlidingMenu history_manager_slidemenu = (HistoryManagerSlidingMenu) convertView
				.findViewById(R.id.history_manager_slidemenu);
		// ɾ������
		ImageButton historyDelete = (ImageButton) convertView.findViewById(R.id.history_manager_delete);
		historyDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String fileName = bean.getPackagename() + "_" + bean.getDate() + ".csv";
				File file = new File("/mnt/sdcard/testtool", fileName);
				if (file.exists()) { // �ж��ļ��Ƿ����
					if (file.isFile()) { // �ж��Ƿ����ļ�
						boolean isDelete = file.delete(); // delete()���� ��Ӧ��֪��
															// ��ɾ������˼;
						if (isDelete) {
							Toast.makeText(mContext, "ɾ���ɹ�", 3000).show();
							history_manager_slidemenu.smoothScrollTo(0, 0);
							// �����̣߳����ظ�app�����в��Լ�¼
							// HistoryApplicationThread historyAppThread = new
							// HistoryApplicationThread(
							// bean.getPackagename(), bean.getAppname());
							// historyAppThread.start();

							// �޸�����
							// TestToolNewApplication.historyDetailApplicationList
							for (int i = 0; i < TestToolNewApplication.historyDetailApplicationList.size(); i++) {
								if (TestToolNewApplication.historyDetailApplicationList.get(i).getDate() == bean
										.getDate()) {
									TestToolNewApplication.historyDetailApplicationList.remove(i);
								}
							}

							// ������ʷ��¼��ҳ������

							ListHistoryAppsThread historyAppsThread = new ListHistoryAppsThread();
							historyAppsThread.start();

							// ���͹㲥�������ݺͽ���
							Intent intent = new Intent();
							intent.setAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST);
							TestToolNewApplication.mySelf.sendStickyBroadcast(intent);

						}
					}
				}

			}
		});
		// //ʵ����ת
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

		// // �����ת�����Թ���ҳ��
		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Log.i("info", "===========�����ת==========");
		// Intent intent = new Intent();
		// intent.setClass(mContext, HistoryManagerDetailActivity.class);
		// mContext.startActivity(intent);
		// }
		// });
		return convertView;
	}

}
