package com.qihoo.testtools_new;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo.testtools_new.adapter.HistoryManagerAdapter;
import com.qihoo.testtools_new.bean.ApplicationBean1;
import com.qihoo.testtools_new.bean.HistoryBean;
import com.qihoo.testtools_new.thread.HistoryRecordAllThread;

public class HistoryManagerActivity extends Activity {

	ListView lv_history;
	HistoryManagerAdapter adapter;
	// ArrayList<ApplicationBean1> data;
	ArrayList<HistoryBean> historyDetailAppList = new ArrayList<HistoryBean>();

	TextView history_manager_back;
	CheckBox history_all_cb;
	Button history_check_btn;

	String receiverAction = "com.qihoo.testtool.new.history.manager.broadcastreceiver1";
	int count = 0;
	HistoryManagerBroadcastReceiver receiver;
	// HistoryBean bean;
	String appname;

	HistoryManagerDataListBroadcastReceiver detailRevceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_manager);
		TestToolNewApplication.historyRecodeDataList.clear();

		Intent intent = this.getIntent();
		appname = intent.getStringExtra("appname");
		// bean = (HistoryBean) intent.getSerializableExtra("bean");

		initView();
		initListener();

		receiver = new HistoryManagerBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(receiverAction);
		registerReceiver(receiver, filter);

		detailRevceiver = new HistoryManagerDataListBroadcastReceiver();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST);
		registerReceiver(detailRevceiver, filter1);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		unregisterReceiver(detailRevceiver);
	}

	private void initView() {

		history_manager_back = (TextView) findViewById(R.id.history_manager_back);
		history_all_cb = (CheckBox) findViewById(R.id.history_all_cb);
		history_check_btn = (Button) findViewById(R.id.history_check_btn);
		lv_history = (ListView) findViewById(R.id.lv_history_manager);

		history_manager_back.setText("" + appname);
		// ģ������
		// data = new ArrayList<ApplicationBean1>();
		// for (int i = 0; i < 10; i++) {
		// ApplicationBean1 bean = new ApplicationBean1();
		// bean.appName = "Ӧ��" + i;
		// bean.appImg = "";
		// bean.data = "1234545";
		// bean.isBAT = true;
		// bean.isCPU = true;
		// bean.isMEM = true;
		// bean.isTRA = true;
		// bean.isChoose = false;
		// data.add(bean);
		//
		// }
		adapter = new HistoryManagerAdapter(this,
				R.layout.history_manager_listview_item, null);
		lv_history.setAdapter(adapter);

	}

	private void initListener() {
		history_check_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (count > 0) {
					// ׼������
					TestToolNewApplication.historyRecodeDataList.clear();
					for (int j = 0; j < historyDetailAppList.size(); j++) {
						HistoryBean bean = historyDetailAppList.get(j);
						if (bean.isChoose) {

							TestToolNewApplication.historyRecodeDataList
									.add(bean);

						}

					}

					// �����߳�
					if (TestToolNewApplication.historyRecodeDataList.size() > 0) {

						// ��������
						// // list����date��������
						Collections.sort(
								TestToolNewApplication.historyRecodeDataList,
								new Comparator<HistoryBean>() {

									@Override
									public int compare(HistoryBean bean1,
											HistoryBean bean2) {
										// if(bean1.getDate()>bean2.getDate()){
										// return 1;
										// }
										return (int) (bean2.getDate() - bean1
												.getDate());

									}
								});
						// ��ת���ܼ�ҳ��
						Intent intent = new Intent();
						intent.setClass(HistoryManagerActivity.this,
								HistoryManagerDetailAllActivity.class);
						intent.putExtra("appname", appname);
						HistoryManagerActivity.this.startActivity(intent);
					} else {
						Toast.makeText(HistoryManagerActivity.this, "û�м�¼",
								3000).show();
					}

				}else {
					Toast.makeText(HistoryManagerActivity.this, "û�м�¼",
							3000).show();
				}
			}
		});

		history_manager_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HistoryManagerActivity.this.finish();

			}
		});
		history_all_cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChoose = ((CheckBox) v).isChecked();
				if (isChoose) {// ѡ�У����������data�����ݣ�������ʾ
					for (HistoryBean bean : historyDetailAppList) {
						bean.isChoose = true;
					}
					count = historyDetailAppList.size();
					//
					// TestToolNewApplication.historyRecodeDataList.clear();
					// TestToolNewApplication.historyRecodeDataList =
					// historyDetailAppList;

					if(count>0){
					history_check_btn.setBackgroundColor(getResources()
							.getColor(R.color.choose_count));
					}
					history_check_btn.setText("ͳ�ƣ�"
							+ historyDetailAppList.size() + "��");
					

				} else {// δѡ�У��޸�data����
					for (HistoryBean bean : historyDetailAppList) {
						bean.isChoose = false;
					}
					count = 0;
					// TestToolNewApplication.historyRecodeDataList.clear();

					history_check_btn.setBackgroundColor(getResources()
							.getColor(R.color.choose));
					history_check_btn.setText("ͳ�ƣ�0��");
				}
				adapter.setdata(historyDetailAppList);
				adapter.notifyDataSetChanged();
			}
		});
	}

	class HistoryManagerBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.qihoo.testtool.new.history.manager.broadcastreceiver1"
					.equals(action)) {
				// ���½���
				boolean isChoose = intent.getBooleanExtra("isChoose", false);
				if (isChoose) {
					count = count + 1;
				} else {
					count = count - 1;
				}
				if (count > 0) {
					history_check_btn.setBackgroundColor(getResources()
							.getColor(R.color.choose_count));
					history_check_btn.setText("ͳ�ƣ�" + count + "��");
				} else {
					history_check_btn.setBackgroundColor(getResources()
							.getColor(R.color.choose));
					history_check_btn.setText("ͳ�ƣ�0��");
				}

				if (count == historyDetailAppList.size()) {
					history_all_cb.setChecked(true);
				} else {
					history_all_cb.setChecked(false);
				}

			}
		}

	}

	class HistoryManagerDataListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_DETIAL_LIST
					.equals(action)) {

				// �������ݺ�listview
				historyDetailAppList = (ArrayList<HistoryBean>) TestToolNewApplication.historyDetailApplicationList;
				adapter.setDataChanged(TestToolNewApplication.historyDetailApplicationList);
				adapter.notifyDataSetChanged();
			}

		}
	}
}
