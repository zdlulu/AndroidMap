package com.example.maptest;

import com.example.maptest.MainActivity.MyHandler;

import android.app.Application;

public class MyApp extends Application{
	
	// 共享变量  
    private MyHandler handler = null;
    private int db_num= 0;
      
    // set方法  
    public void setHandler(MyHandler handler) {  
        this.handler = handler;  
    }  
      
    // get方法  
    public MyHandler getHandler() {  
        return handler;  
    } 
}
