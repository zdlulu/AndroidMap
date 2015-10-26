package com.example.search;

import com.example.maptest.MainActivity.MyHandler;
import com.example.maptest.Messages;
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
import android.widget.EditText;

public class SearchActivity extends Activity implements OnClickListener{
	public static final String action = "jason.broadcast.SearchActivity";  
	Button btn_sear_result;
	private MyApp mAPP = null;  
    private MyHandler sear_handler = null;  
    private EditText et_sear_key,et_sear_city;
    String[] sear_str = new String[2];
	
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
		et_sear_city = (EditText) findViewById(R.id.et_sear_city);
		et_sear_key = (EditText) findViewById(R.id.et_sear_key);
		et_sear_city.setText("天津");
		et_sear_key.setText("九安");
	}

	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.btn_sear_result:
			sear_str[0] = et_sear_city.getText().toString();
			sear_str[1] = et_sear_key.getText().toString();
			Message msg = new Message();
			msg.what = Messages.MSG1;
			msg.obj = sear_str;
			sear_handler.sendMessage(msg);  
			Intent intent = new Intent(action);  
            intent.putExtra("data", "yes i am data");  
            sendBroadcast(intent);  
            finish();  
			break;
		}
	}

}
