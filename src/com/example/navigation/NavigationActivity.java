package com.example.navigation;

import java.util.ArrayList;
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
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
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
    //�������
    RoutePlanSearch mSearch = null;    // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
    PlanNode stNode,enNode;
    private EditText et_nav_origin,et_nav_destination;
    ArrayList<TransitStep> steps =null;
    ArrayList<Integer>MultiChoiceID = new ArrayList<Integer>();  
    public String[] nItems = null;
    private String line_nitem="";
    RouteLine[] route = null;
    RouteLine choose_route = null;
    DBHelper db;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navigation_activity);
		mAPP = (MyApp) getApplication(); 
		// ��øù������ʵ��  
		navi_handler = mAPP.getHandler();  
		nav_init();
		db = new DBHelper(this);   
		
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
		et_nav_destination = (EditText) findViewById(R.id.et_nav_destination);
		tv_line = (TextView) findViewById(R.id.tv_line);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_nav_result:
			//���ص�ͼ
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
			//������Ϣ
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
        	route = new RouteLine[line_size];
        	for(int i=0;i<line_size;i++){
        		route[i] = transit_result.getRouteLines().get(i);
        		steps = (ArrayList<TransitStep>) route[i].getAllStep(); 
        		for (TransitStep step : steps) {
        			line_str = line_str+step.getInstructions();
//        			Log.i("line_str=","i="+line);
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
        builder.setTitle("����ѡ��");
        builder.setSingleChoiceItems(nItems, 1,  new DialogInterface. OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				line_nitem = nItems[which];
				choose_route = route[which];
				Toast.makeText(getApplicationContext(), "��ѡ���IDΪ��"+which, Toast.LENGTH_SHORT).show();  
			}});
        //����ȷ����ť  
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAPP.setFlag(true);
				db.delete("1");
				tv_line.setText(line_nitem);
				db.save(line_nitem);
				Message msg = new Message();
				msg.obj = choose_route;
				msg.what = Messages.MSG3;
				navi_handler.sendMessage(msg);  
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
        if(mAPP.getFlag()){
          Cursor cur = db.loadAll();   
          StringBuffer sf = new StringBuffer();   
          cur.moveToFirst();   
//          while (!cur.isAfterLast()) {   
              sf.append(cur.getString(1)).append("\n");   
              cur.moveToNext();   
//          }  
          tv_line.setText(sf.toString());
        }else{
        	db.delete("0");
        }

    }

    /*************************************************************/
}
