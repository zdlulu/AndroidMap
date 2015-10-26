package com.example.search;

import java.util.List;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.maptest.MainActivity.MyHandler;
import com.example.maptest.Messages;
import com.example.maptest.MyApp;
import com.example.mymaptest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
  

public class SearchActivity extends Activity implements OnClickListener,	
							OnGetPoiSearchResultListener{
	public static final String action = "jason.broadcast.SearchActivity";  
	private PoiSearch mPoiSearch = null;
	Button btn_sear_result,btn_sear_back;
	private MyApp mAPP = null;  
    private MyHandler sear_handler = null;  
    private EditText et_sear_key,et_sear_city;
    String[] sear_str = new String[2];
    private static StringBuilder sb;  
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		mAPP = (MyApp) getApplication(); 
		// 获得该共享变量实例  
		sear_handler = mAPP.getHandler();  
		sear_init();
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		btn_sear_result.setOnClickListener(this);
		btn_sear_back.setOnClickListener(this);
		sb = new StringBuilder();  
	}
	
 	public void sear_init(){
		btn_sear_result = (Button) findViewById(R.id.btn_sear_result);
		btn_sear_back = (Button) findViewById(R.id.btn_sear_back);
		et_sear_city = (EditText) findViewById(R.id.et_sear_city);
		et_sear_key = (EditText) findViewById(R.id.et_sear_key);
		et_sear_city.setText("天津");
		et_sear_key.setText("九安");
	}

	@Override
	public void onClick(View v) {		
		switch(v.getId()){
		case R.id.btn_sear_back:
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
		case R.id.btn_sear_result:
			mPoiSearch.searchInCity((new PoiCitySearchOption())
					.city(et_sear_city.getText().toString())
					.keyword(et_sear_key.getText().toString())
					.pageCapacity(10));
			break;
		}
	}
	
	/*************************************************************/
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		int i=0;
		if(result.getCurrentPageNum() == 0) {
			sb.append("共搜索到").append(result.getTotalPoiNum()).append("个POI\n");  
		}
//		Log.i("result="+result.getTotalPageNum(), "20151026");
		for(PoiInfo po:result.getAllPoi()){
			i++;
			sb.append(String.valueOf(i)+"名称：").append(po.name)
							.append(po.address).append("\n");  
		}
		// 通过AlertDialog显示所有搜索到的POI  
        new AlertDialog.Builder(SearchActivity.this)  
        .setTitle("搜索到的POI信息")  
        .setMessage(sb.toString())  
        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                dialog.dismiss();  
            }  
        }).create().show();  
	}
	/*************************************************************/

}
