package com.example.maptest;

import com.example.maptest.MainActivity.MyHandler;

import android.app.Application;

public class MyApp extends Application{
	
	// �������  
    private MyHandler handler = null;
    private boolean flag_resume=false;;
      
    // set����  
    public void setHandler(MyHandler handler) {  
        this.handler = handler;  
    }  
      
    // get����  
    public MyHandler getHandler() {  
        return handler;  
    } 
    
    public void set_flag(boolean a){
    	this.flag_resume = a;
    }
    public boolean get_flag(){
    	return flag_resume;
    }
}
