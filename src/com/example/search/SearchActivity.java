package com.example.search;

import com.example.mymaptest.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SearchActivity extends Activity implements OnClickListener{
	public static final String action = "jason.broadcast.SearchActivity";  
	Button btn_sear_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
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
			Intent intent = new Intent(action);  
            intent.putExtra("data", "yes i am data");  
            sendBroadcast(intent);  
            finish();  
			break;
		}
	}

}
