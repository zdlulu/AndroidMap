package com.example.maptest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
	private static final String TAG = "DBDemo_DBHelper";// 调试标签   
	   
    private static final String DATABASE_NAME = "dbdemo.db";// 数据库名   
    SQLiteDatabase db;   
    Context context;//应用环境上下文   Activity 是其子类   
    
    public DBHelper(Context _context) {   
    	context = _context;   
        //开启数据库   
            
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);   
        CreateTable();   
        Log.v(TAG, "db path=" + db.getPath());    
    }   
   
    /**  
     * 建表  
     * 列名 区分大小写？  
     * 都有什么数据类型？  
     * SQLite 3   
     *  TEXT    文本  
        NUMERIC 数值  
        INTEGER 整型  
        REAL    小数  
        NONE    无类型  
     * 查询可否发送select ?  
     */
    public void CreateTable() {
    	try { 
    		db.execSQL("CREATE TABLE t_user (" + 
    				"_ID INTEGER PRIMARY KEY autoincrement,"
    				+ "NAME TEXT"
    				+ ");");
    		Log.v(TAG, "Create Table t_user ok");
    	} catch (Exception e) { 
    		Log.v(TAG, "Create Table t_user err,table exists.");   
        }
    }   
    /**  
     * 增加数据  
     * @param id  
     * @param uname  
     * @return  
     */   
    public boolean save(String uname){   
        String sql="";   
        try{   
            sql="insert into t_user values(null,'"+uname+"')";   
            db.execSQL(sql);   
            Log.v(TAG,"insert Table t_user ok");   
            return true;   
               
        }catch(Exception e){   
            Log.v(TAG,"insert Table t_user err ,sql: "+sql);   
            return false;   
        }   
    }   
    /**  
     * 删除相应的记录  
     *   
     *   一种是按照_ID删除
     *   一种是按照NAME删除
     */ 
//    public void delete(String NAME){
//    	db.delete("t_user", "NAME=?", new String[]{NAME});
//    }
    public void delete(String _ID){
    	db.delete("t_user", "_ID=?", new String[]{_ID});
    }
    
    /**  
     * 查询所有记录  
     *   
     * @return Cursor 指向结果记录的指针，类似于JDBC 的 ResultSet  
     */   
    public Cursor loadAll(){
    	Cursor cur=db.query("t_user", new String[]{"_ID","NAME"}, null,null, null, null, null);    
        return cur;   
    }
    
    public void close(){
    	db.close();
    }
    
    /**
    * 删除数据库
    * 
    * @param context
    * @return
    */
    public boolean deleteDatabase(Context context) {
    	return context.deleteDatabase(DATABASE_NAME);
    }
      
}
