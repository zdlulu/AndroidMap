package com.example.navigation;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.maptest.Messages;
import com.example.maptest.MyApp;
import com.example.maptest.MainActivity.MyHandler;
import com.example.mymaptest.R;
import com.example.search.SearchActivity;

public class NavigationActivity extends Activity implements OnClickListener,
					OnGetRoutePlanResultListener{
	public static final String action = "jason.broadcast.NavigationActivity";  
	Button btn_nav_result,btn_nav_transit,btn_nav_walk,btn_nav_drive;
	private MyApp mAPP = null;  
    private MyHandler navi_handler = null; 
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    PlanNode stNode,enNode;
    private EditText et_nav_origin,et_nav_destination;
    ArrayList<TransitStep> steps =null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_activity);
		mAPP = (MyApp) getApplication(); 
		// 获得该共享变量实例  
		navi_handler = mAPP.getHandler();  
		nav_init();
		
		btn_nav_result.setOnClickListener(this);
		btn_nav_walk.setOnClickListener(this);
		btn_nav_transit.setOnClickListener(this);
		btn_nav_drive.setOnClickListener(this);
		
		// 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
		stNode = PlanNode.withCityNameAndPlaceName("天津", et_nav_origin.getText().toString());
		enNode = PlanNode.withCityNameAndPlaceName("天津", et_nav_destination.getText().toString());
	}
	
	public void nav_init(){
		btn_nav_result = (Button) findViewById(R.id.btn_nav_result);
		btn_nav_walk = (Button) findViewById(R.id.btn_nav_walk);
		btn_nav_transit = (Button) findViewById(R.id.btn_nav_transit);
		btn_nav_drive = (Button) findViewById(R.id.btn_nav_drive);
		et_nav_origin = (EditText) findViewById(R.id.et_nav_origin);;
		et_nav_destination = (EditText) findViewById(R.id.et_nav_destination);;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_nav_result:
			Message msg = new Message();
			msg.what = Messages.MSG2;
			navi_handler.sendMessage(msg);  
			Intent intent = new Intent(action);  
            intent.putExtra("data", "yes i am data");  
            sendBroadcast(intent);  
            finish();  
			break;
		case R.id.btn_nav_drive:
			break;
		case R.id.btn_nav_transit:
			mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city("天津")
                    .to(enNode));
			break;
		case R.id.btn_nav_walk:
			break;
		}
		
	}
	
	/*OnGetRoutePlanResultListener********************************/
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult drive_result) {
		
	}
	@Override
	public void onGetTransitRouteResult(TransitRouteResult transit_result) {
		
		if (transit_result == null || transit_result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(NavigationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (transit_result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
        	transit_result.getSuggestAddrInfo();
            return;
        }
        if (transit_result.error == SearchResult.ERRORNO.NO_ERROR) {
        	int line_size = transit_result.getRouteLines().size();
        	String line_str = "";
        	for(int i=0;i<line_size;i++){
        		TransitRouteLine line = transit_result.getRouteLines().get(i);
        		RouteNode start = line.getStarting();
        		RouteNode end = line.getTerminal();
        		steps = (ArrayList<TransitStep>) line.getAllStep(); 
        		line_str = line_str+String.valueOf(i);
        		for (TransitStep step : steps) {
        			line_str = line_str+step.getInstructions()+"\n";
//        			Log.i(""+step.getInstructions(),"20151026");
        		}
        		
        	}
        	// 通过AlertDialog显示所有搜索到的POI  
            new AlertDialog.Builder(NavigationActivity.this)  
            .setTitle("搜索到的POI信息")  
            .setMessage(line_str)  
            .setPositiveButton("关闭", new DialogInterface.OnClickListener() {  
                public void onClick(DialogInterface dialog, int whichButton) {  
                    dialog.dismiss();  
                }  
            }).create().show();  
        }
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult walk_result) {
		
	}
	/*************************************************************/
}
