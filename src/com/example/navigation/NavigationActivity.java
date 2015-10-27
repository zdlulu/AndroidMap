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

public class NavigationActivity extends Activity implements OnClickListener,
					OnGetRoutePlanResultListener{
	public static final String action = "jason.broadcast.NavigationActivity";  
	Button btn_nav_result,btn_nav_transit,btn_nav_walk,btn_nav_drive;
	private MyApp mAPP = null;  
    private MyHandler navi_handler = null; 
    //�������
    RoutePlanSearch mSearch = null;    // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
    PlanNode stNode,enNode;
    private EditText et_nav_origin,et_nav_destination;
    ArrayList<TransitStep> steps =null;
    ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();  
    public String[] nItems = null;
    public boolean[] nItems_boolean= null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_activity);
		mAPP = (MyApp) getApplication(); 
		// ��øù������ʵ��  
		navi_handler = mAPP.getHandler();  
		nav_init();
		
		btn_nav_result.setOnClickListener(this);
		btn_nav_walk.setOnClickListener(this);
		btn_nav_transit.setOnClickListener(this);
		btn_nav_drive.setOnClickListener(this);
		
		// ��ʼ������ģ�飬ע���¼�����
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
		stNode = PlanNode.withCityNameAndPlaceName("���", et_nav_origin.getText().toString());
		enNode = PlanNode.withCityNameAndPlaceName("���", et_nav_destination.getText().toString());
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
                    .city("���")
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
            Toast.makeText(NavigationActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (transit_result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
        	transit_result.getSuggestAddrInfo();
            return;
        }
        if (transit_result.error == SearchResult.ERRORNO.NO_ERROR) {
        	int line_size = transit_result.getRouteLines().size();
        	String line_str = "",step_str="";
        	nItems = new String[line_size];
        	nItems_boolean = new boolean[line_size];
        	for(int i=0;i<line_size;i++){
        		TransitRouteLine line = transit_result.getRouteLines().get(i);
        		steps = (ArrayList<TransitStep>) line.getAllStep(); 
        		for (TransitStep step : steps) {
        			line_str = line_str+step.getInstructions();
//        			Log.i("line_str=","i="+line);
        		}
        		step_str = step_str+String.valueOf(i)+line_str+"\n";
        		nItems[i] = String.valueOf(i)+line_str;
        		nItems_boolean[i] = false;
        		Log.i("line_str","="+String.valueOf(i)+line_str);
        		
        		line_str = "";
        	}
        	get_item();
//        	// ͨ��AlertDialog��ʾ������������POI  
//            new AlertDialog.Builder(NavigationActivity.this)  
//            .setTitle("��������POI��Ϣ")  
//            .setMessage(step_str)  
//            .setPositiveButton("�ر�", new DialogInterface.OnClickListener() {  
//                public void onClick(DialogInterface dialog, int whichButton) {  
//                    dialog.dismiss();  
//                }  
//            }).create().show();  
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
        builder.setTitle("����ѡ��");
        builder.setMultiChoiceItems(nItems,nItems_boolean,
        		new DialogInterface.OnMultiChoiceClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
						if (arg2) {
		        			MultiChoiceID.add(arg1);
		        			String tip = "��ѡ���ǣ�"+nItems[arg1];  
		                    Toast toast = Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT);  
		                    toast.show();
		        		}else {
		        			MultiChoiceID.remove(arg1);
		        			}
		        		}
					});
        //����ȷ����ť  
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
        	
        });
      //����ȡ����ť  
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
                  
            }  
        });  
        builder.create().show();
	}
	/*************************************************************/
}
