package com.example.mymaptest;

import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
		// ע��÷���Ҫ��setContentView����֮ǰʵ��  
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.main);
	}
}
