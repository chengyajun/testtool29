package com.qihoo.testtools_new;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AdviceActivity extends Activity {

	TextView history_manager_back;
	EditText advice_content_et, advice_contact_et;
	Button advice_submit_btn;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advice_layout);
		initView();
		initListener();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				int what = msg.what;// -1：异常 0:失败 1：成功
				switch (what) {
				case -1: {
					Toast.makeText(AdviceActivity.this, "发送异常", 3000).show();
				}
					break;
				case 0: {
					Toast.makeText(AdviceActivity.this, "发送失败", 3000).show();
				}
					break;
				case 1: {
					Toast.makeText(AdviceActivity.this, "发送成功", 3000).show();
				}
					break;

				}
			}

		};
	}

	private void initView() {

		history_manager_back = (TextView) findViewById(R.id.history_manager_back);
		advice_content_et = (EditText) findViewById(R.id.advice_content_et);
		advice_contact_et = (EditText) findViewById(R.id.advice_contact_et);
		advice_submit_btn = (Button) findViewById(R.id.advice_submit_btn);
	}

	private void initListener() {
		advice_submit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 校验输入框
				final String content = advice_content_et.getText().toString().trim();
				final String contact = advice_contact_et.getText().toString().trim();
				if (content.equals("")) {
					advice_content_et.setError("内容不能为空");
					return;
				}
				if (contact.equals("")) {
					advice_contact_et.setError("联系方式不能为空");
					return;
				}
				// 联网进行数据传送

				new Thread() {
					public void run() {
						// 将反馈信息传送到服务器
						HttpClient httpclient = new DefaultHttpClient();
						HttpEntity httpentity = null;
						HttpResponse httpResponse = null;
						try {
							HttpPost post = new HttpPost("http://172.17.103.3:8080/AdviceServer/advice");
							// 数据
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("type", "advice"));
							params.add(new BasicNameValuePair("content", content));
							params.add(new BasicNameValuePair("contact", contact));
							httpentity = new UrlEncodedFormEntity(params, "utf-8");
							post.setEntity(httpentity);
							httpResponse = httpclient.execute(post);
							// 当然也可以根据服务器返回的信息判断，此处默认200表示发送成功
							if (httpResponse.getStatusLine().getStatusCode() == 200) {
								handler.sendEmptyMessage(1);
								Log.i("info", "发送成功");
							} else {
								handler.sendEmptyMessage(0);
								Log.i("info", "发送失败");
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(-1);
							Log.i("info", "发送出现异常");
							e.printStackTrace();
						}
					};
				}.start();

			}
		});

		history_manager_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AdviceActivity.this.finish();

			}
		});

	}

}
