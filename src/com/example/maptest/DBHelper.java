package com.example.maptest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {
	private static final String TAG = "DBDemo_DBHelper";// ���Ա�ǩ   
	   
    private static final String DATABASE_NAME = "dbdemo.db";// ���ݿ���   
    SQLiteDatabase db;   
    Context context;//Ӧ�û���������   Activity ��������   
    
    public DBHelper(Context _context) {   
    	context = _context;   
        //�������ݿ�   
            
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE,null);   
        CreateTable();   
        Log.v(TAG, "db path=" + db.getPath());    
    }   
   
    /**  
     * ����  
     * ���� ���ִ�Сд��  
     * ����ʲô�������ͣ�  
     * SQLite 3   
     *  TEXT    �ı�  
        NUMERIC ��ֵ  
        INTEGER ����  
        REAL    С��  
        NONE    ������  
     * ��ѯ�ɷ���select ?  
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
     * ��������  
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
     * ɾ����Ӧ�ļ�¼  
     *   
     *   һ���ǰ���_IDɾ��
     *   һ���ǰ���NAMEɾ��
     */ 
//    public void delete(String NAME){
//    	db.delete("t_user", "NAME=?", new String[]{NAME});
//    }
    public void delete(String _ID){
    	db.delete("t_user", "_ID=?", new String[]{_ID});
    }
    
    /**  
     * ��ѯ���м�¼  
     *   
     * @return Cursor ָ������¼��ָ�룬������JDBC �� ResultSet  
     */   
    public Cursor loadAll(){
    	Cursor cur=db.query("t_user", new String[]{"_ID","NAME"}, null,null, null, null, null);    
        return cur;   
    }
    
    public void close(){
    	db.close();
    }
    
    /**
    * ɾ�����ݿ�
    * 
    * @param context
    * @return
    */
    public boolean deleteDatabase(Context context) {
    	return context.deleteDatabase(DATABASE_NAME);
    }
      
}
