package com.example.maptest;

import com.example.maptest.MainActivity.MyHandler;

import android.app.Application;

public class MyApp extends Application{
	
	// �������  
    private MyHandler handler = null;
    private boolean flag = false;
      
    // set����  
    public void setHandler(MyHandler handler) {  
        this.handler = handler;  
    }  
      
    // get����  
    public MyHandler getHandler() {  
        return handler;  
    } 
    
    public void setFlag(boolean flag){
    	this.flag = flag;
    }
    public boolean getFlag(){
    	return flag;
    }
    
    
    
}
