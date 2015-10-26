package com.example.search;

import com.example.maptest.MainActivity.MyHandler;
import com.example.maptest.MyApp;
import com.example.mymaptest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SearchActivity extends Activity implements OnClickListener{
	public static final String action = "jason.broadcast.SearchActivity";  
	Button btn_sear_result;
	private MyApp mAPP = null;  
    private MyHandler sear_handler = null;  
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		mAPP = (MyApp) getApplication(); 
		// 获得该共享变量实例  
		sear_handler = mAPP.getHandler();  
		sear_init();
		btn_sear_result.setOnClickListener(this);
	}
	
	public void sear_init(){
		btn_sear_result = (Button) findViewById(R.id.btn_sear_result);
	}

	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.btn_sear_result:
			Log.i("0x03_send", "20151026");
			Message msg = new Message();
			msg.what = 0x03;
			sear_handler.sendMessage(msg);  
			Intent intent = new Intent(action);  
            intent.putExtra("data", "yes i am data");  
            sendBroadcast(intent);  
            finish();  
			break;
		}
	}

}
