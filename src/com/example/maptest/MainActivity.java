package com.example.maptest;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.mymaptest.R;
import com.example.navigation.NavigationActivity;
import com.example.search.SearchActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MainActivity extends Activity implements OnGetPoiSearchResultListener,	
	OnGetSuggestionResultListener,OnClickListener{
	MapView bMapView;
	BaiduMap mBaiduMap;
	private LocationMode mCurrentMode;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = true;// 是否首次定位
	private Button zoomin,zoomout;
	private Button btn_intent_search,btn_intent_navigation;
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private float zoomLevel;
	private MyHandler handler = null;  
    private MyApp mAPP = null;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext  
		// 注意该方法要再setContentView方法之前实现  
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		SDKInitializer.initialize(getApplicationContext()); 
		setContentView(R.layout.main);
		mAPP = (MyApp) getApplication();  
        handler = new MyHandler();  
        mAPP.setHandler(handler); 
		init_widget();
		
		btn_intent_search.setOnClickListener(this);
		btn_intent_navigation.setOnClickListener(this);
		zoomin.setOnClickListener(this);
		zoomout.setOnClickListener(this);
		
		
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		// 地图初始化
		mBaiduMap = bMapView.getMap();
		hideZoomView(bMapView);
		zoomLevel = mBaiduMap.getMapStatus().zoom;
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
	    mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		/*************************************************/
	}
	
	/* 定位SDK监听函数**************************************************/
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || bMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}
	/*********************************************************************/
	public void init_widget(){
		bMapView=(MapView)findViewById(R.id.id_bmapView);//找到控件视图  
		zoomin = (Button) findViewById(R.id.zoomin);
		zoomout = (Button) findViewById(R.id.zoomout);
		btn_intent_search = (Button) findViewById(R.id.btn_intent_search);
		btn_intent_navigation = (Button) findViewById(R.id.btn_intent_navigation);
		mCurrentMode = LocationMode.NORMAL;
//		zoomin.setShadowLayer(5, 0, 0, 0xff00ff00);   // 设置字体阴影的半径和颜色
		zoomin.setTextAppearance(this, R.style.AppTheme);   // 设置字体的style
	}
	/*********************************************************************/
	/*********************************************************************/
	/*********************************************************************/
	/*********************************************************************/
	/*********************************************************************/
	/*********************************************************************/
	/*PoiSearch******OnGetPoiSearchResultListener*************************/
	@Override
	//点击相应红色的目标，弹窗显示具体的信息
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(MainActivity.this, result.getName() + ":" + result.getAddress(), Toast.LENGTH_SHORT)
			.show();
		}
	}
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
			.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
//			Log.i("num="+result.getCurrentPageNum(), "20151022");
//			Log.i("result="+result.getCurrentPageCapacity(), "20151022");
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}
	/*mSuggestionSearch**********OnGetSuggestionResultListener************/
	@Override
	public void onGetSuggestionResult(SuggestionResult arg0) {
		
	}
	/*********************************************************************/
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			Log.i("result="+getPoiResult().getAllPoi().size(), "20151026");
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			return true;
		}
	}
	/*********************************************************************/
	/**
	 * 隐藏缩放控件
	 * 
	 * @param mapView
	 */
	private void hideZoomView(MapView mapView) {
		// 隐藏缩放控件
		int childCount = mapView.getChildCount();
		View zoom = null;
		for (int i = 0; i < childCount; i++) {
			View child = mapView.getChildAt(i);
			if (child instanceof ZoomControls) {
				zoom = child;
				break;
			}
		}
		zoom.setVisibility(View.GONE);
	}
	/*********************************************************************/
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_intent_navigation:
			Intent intent_na = new Intent();
			intent_na.setClass(MainActivity.this,NavigationActivity.class);
			startActivityForResult(intent_na, 1);
			break;
		case R.id.btn_intent_search:
			Intent intent_se = new Intent();
			intent_se.setClass(MainActivity.this,SearchActivity.class);
			startActivityForResult(intent_se, 1);
			break;
		case R.id.zoomout:
			if(zoomLevel<=18){
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
				zoomout.setEnabled(true);
			}else{
				Toast.makeText(MainActivity.this, "已经放至最大！", Toast.LENGTH_SHORT).show();
				zoomout.setEnabled(false);
			}
			break;
		case R.id.zoomin:
			if(zoomLevel>4){
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
				zoomin.setEnabled(true);
			}else{
				zoomin.setEnabled(false);
				Toast.makeText(MainActivity.this, "已经缩至最小！", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	/*********************************************************************/
	public class MyHandler extends Handler {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch(msg.what){
            case Messages.MSG1:
            	String[] str = (String[]) msg.obj;
            	mPoiSearch.searchInCity((new PoiCitySearchOption())
            			.city(str[0])
            			.keyword(str[1])
            			.pageCapacity(15));
            	break;
            }
        }  
    }  
	/*********************************************************************/
	/*********************************************************************/
}
