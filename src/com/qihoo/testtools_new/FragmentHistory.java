package com.qihoo.testtools_new;

import java.util.ArrayList;

import com.qihoo.testtools_new.adapter.AppsAdapter;
import com.qihoo.testtools_new.adapter.HistoryAdapter;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentHistory extends Fragment {
	View rootView;
	ListView lv_history;
	HistoryAdapter adapter;
	ArrayList<String> data;
	HistoryAppsDataListBroadcastreceiver receiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_history, null);
		initView();

		receiver = new HistoryAppsDataListBroadcastreceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST);
		getActivity().registerReceiver(receiver, filter);
		return rootView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

	private void initView() {
		lv_history = (ListView) rootView.findViewById(R.id.lv_history);

		// 模拟数据
		// data = new ArrayList<String>();
		// for (int i = 0; i < 10; i++) {
		// data.add("应用" + i);
		// }
		adapter = new HistoryAdapter(getActivity(),
				R.layout.history_listview_item, null);
		lv_history.setAdapter(adapter);

	}

	// 广播接收器，获取所有手机测试记录，更新数据和界面

	class HistoryAppsDataListBroadcastreceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TestToolNewApplication.INTENT_ACTION_BROADCAST_HISTORY_APPS_LIST
					.equals(action)) {
				// 更新数据和界面
				adapter.setDataChanged(TestToolNewApplication.historyAppsList);
				adapter.notifyDataSetChanged();
			}

		}

	}

}
