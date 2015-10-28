package com.example.navigation;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.maptest.DBHelper;
import com.example.maptest.Messages;
import com.example.maptest.MyApp;
import com.example.maptest.MainActivity.MyHandler;
import com.example.mymaptest.R;

public class NavigationActivity extends Activity implements OnClickListener,
					OnGetRoutePlanResultListener{
	public static final String action = "jason.broadcast.NavigationActivity";  
	private Button btn_nav_result,btn_nav_transit,btn_nav_walk,btn_nav_drive;
	private TextView tv_line;
	private MyApp mAPP = null;  
    private MyHandler navi_handler = null; 
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    PlanNode stNode,enNode;
    private EditText et_nav_origin,et_nav_destination;
    ArrayList<TransitStep> steps =null;
    ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();  
    public String[] nItems = null;
    private String line_nitem="";
    RouteLine[] route = null;
    RouteLine choose_route = null;
    DBHelper db;
    private int line_size=0;
    PoiInfo poiinfo_sn,poiinfo_en;
    private int  represent_int =0;
    private boolean flag_en=false,flag_sn=false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_activity);
		mAPP = (MyApp) getApplication(); 
		// 获得该共享变量实例  
		navi_handler = mAPP.getHandler();  
		nav_init();
		db = new DBHelper(this);   
		
		btn_nav_result.setOnClickListener(this);
		btn_nav_walk.setOnClickListener(this);
		btn_nav_transit.setOnClickListener(this);
		btn_nav_drive.setOnClickListener(this);
		
		// 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
	}
	
	public void nav_init(){
		btn_nav_result = (Button) findViewById(R.id.btn_nav_result);
		btn_nav_walk = (Button) findViewById(R.id.btn_nav_walk);
		btn_nav_transit = (Button) findViewById(R.id.btn_nav_transit);
		btn_nav_drive = (Button) findViewById(R.id.btn_nav_drive);
		et_nav_origin = (EditText) findViewById(R.id.et_nav_origin);;
		et_nav_destination = (EditText) findViewById(R.id.et_nav_destination);
		tv_line = (TextView) findViewById(R.id.tv_line);
		et_nav_origin.setText("金刚桥");
		et_nav_destination.setText("滨江道");
	}

	@Override
	public void onClick(View v) {
		stNode = PlanNode.withCityNameAndPlaceName("天津", et_nav_origin.getText().toString());
		enNode = PlanNode.withCityNameAndPlaceName("天津", et_nav_destination.getText().toString());
		switch(v.getId()){
		case R.id.btn_nav_result:
			//返回地图
			Message msg = new Message();
			msg.what = Messages.MSG2;
			navi_handler.sendMessage(msg);  
			Intent intent = new Intent(action);  
            intent.putExtra("data", "yes i am data");  
            sendBroadcast(intent);  
            finish();  
			break;
		case R.id.btn_nav_drive:
			//驾车信息 
			represent_int = 2;
			tv_line.setText("");
			mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
			break;
		case R.id.btn_nav_transit:
			//公交信息
			represent_int = 1;
			mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city("天津")
                    .to(enNode));
			break;
		case R.id.btn_nav_walk:
			represent_int = 3;
			break;
		}
		
	}
	
	/*OnGetRoutePlanResultListener********************************/
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult drive_result) {
		
		if (drive_result == null) {
			Toast.makeText(NavigationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (drive_result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			//起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			SuggestAddrInfo mysuggest = drive_result.getSuggestAddrInfo();
			/******************************************************/
            List<PoiInfo> list_sn = mysuggest.getSuggestStartNode();
            List<PoiInfo> list_en = mysuggest.getSuggestEndNode();
            /*起点有歧义的时候*******************************************/
            if(list_sn!=null){
            	flag_sn = true; 
            	int size_sn = list_sn.size();
                if(size_sn>0){
                    Log.i("size_sn="+size_sn, "20151028");
                    nItems = new String[size_sn];
                    for(int i=0;i<size_sn;i++){
                    	poiinfo_sn = list_sn.get(i);
                    	nItems[i] = poiinfo_sn.name;
//                    	Log.i("sn="+poiinfo_sn.name, "20151027");
                    }
                }
            }
            /*终点有歧义的时候*******************************************/
            if(list_en!=null){
            	flag_en = true;
            	int size_en = list_en.size();
            	Log.i("size_en="+size_en, "20151028");
            	nItems = new String[size_en];
                for(int i=0;i<size_en;i++){
                	poiinfo_en = list_en.get(i);
                	nItems[i] = poiinfo_en.name;
                	Log.i("en="+poiinfo_en.name, "20151027");
                }
            }
            /*****************************************************/
            get_item();
			return;
	    }
		if (drive_result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.i("onGetDrivingRouteResult", "20151028");
		}
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
        	line_size = transit_result.getRouteLines().size();
        	String line_str = "",step_str="";
        	nItems = new String[line_size];
        	route = new RouteLine[line_size];
        	for(int i=0;i<line_size;i++){
        		route[i] = transit_result.getRouteLines().get(i);
        		steps = (ArrayList<TransitStep>) route[i].getAllStep(); 
        		for (TransitStep step : steps) {
        			line_str = line_str+step.getInstructions();
        		}
        		step_str = step_str+String.valueOf(i)+line_str+"\n";
        		nItems[i] = String.valueOf(i)+line_str;
        		Log.i("line_str","="+String.valueOf(i)+line_str);
        		line_str = "";
        	}
        	get_item();
        }
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult walk_result) {
		
	}
	/*************************************************************/
	public void get_item(){
		AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this,R.style.dialog);
		MultiChoiceID.clear();  
		builder.setIcon(R.drawable.back);
        builder.setTitle("单项选择");
        builder.setSingleChoiceItems(nItems, 1,  new DialogInterface. OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				line_nitem = nItems[which];
				if(represent_int==1){
					choose_route = route[which];
					Toast.makeText(getApplicationContext(), "你选择的ID为："+which, Toast.LENGTH_SHORT).show();  
				}
			}});
        //设置确定按钮  
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(represent_int==1){
					tv_line.setText(line_nitem);
					for(int i=1;i<line_size;i++){
						db.delete(String.valueOf(i));
					}
					db.save(line_nitem);
					Message msg = new Message();
					msg.obj = choose_route;
					msg.what = Messages.MSG3;
					navi_handler.sendMessage(msg);  
				}else if(represent_int==2){
					if(flag_sn){
						et_nav_origin.setText(line_nitem);
					}
					if(flag_en){
						et_nav_destination.setText(line_nitem);
					}
					
					Log.i("represent_int==2", "20151028");
				}
				
			}
        	
        });
        //设置取消按钮  
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {  
              
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
                  
            }  
        });  
        builder.create().show();
	}
	/*************************************************************/
	@Override
    protected void onPause() {
        super.onPause();
    }
	
	@Override
    protected void onDestroy() {
        super.onPause();
    }
	
    @Override
    protected void onResume() {
    	super.onResume();
    	if(represent_int==1){
    		Cursor cur = db.loadAll();   
            StringBuffer sf = new StringBuffer();   
            cur.moveToFirst();   
            while (!cur.isAfterLast()) {   
                sf.append(cur.getString(1)).append("\n");   
                cur.moveToNext();   
            }    
            cur.moveToNext();  
            tv_line.setText(sf.toString());
    	}
          
    }

    /*************************************************************/
}
