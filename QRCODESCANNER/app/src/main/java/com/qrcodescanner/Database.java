package com.qrcodescanner;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qrcodescanner.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static androidx.constraintlayout.widget.StateSet.TAG;

public class Database{




	private Context ctx;
	public SQLiteDatabase db;


	public Database(Context ctx){
		this.ctx=ctx;
		db = ctx.openOrCreateDatabase("QrCode",ctx.MODE_PRIVATE,null);
		config();

	}


	private void config(){


		jquery("create table if not exists scans(num integer primary key autoincrement not null,value text not null,type text not null)");

	}

	private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
	public int addScan(String value, String type, Bitmap bmp){
        int res=-1;
		jquery("insert into scans(value,type) values('"+value+"','"+type+"')");
		Cursor ins=dquery("select last_insert_rowid()");
		if(ins.moveToNext()){
			res=ins.getInt(0);
		
			try{
				File folder = ctx.getExternalFilesDir("QrCodeReader");
				
	          
		        // Creating file with name gfg.txt
		        File file = new File(folder, "ImageFile"+res+".jpeg");
		        writeData(file, bmp);


			}catch (Exception e){
				e.printStackTrace();
			}
		}

        return res;
	}

	public void jquery(String query)
	{
		try{
			db.execSQL(query);
		}catch(SQLException e)
		{
			MainActivity.disp(e.toString()+" from "+query);
			Log.d(TAG,e.toString());
		}
	}

	public Cursor dquery(String query)
	{
		Cursor ans=null;
		try{
			ans=db.rawQuery(query,null);
		}catch(SQLException e)
		{
			MainActivity.disp(e.toString()+" from "+query);
			Log.d(TAG,e.toString());
			Log.d(TAG, "dquery: ");
		}
		return ans;
	}


	private void writeData(File file, Bitmap bmp) throws Exception {
		try{
			file.getParentFile().mkdirs();
		}catch(Exception e){
			e.printStackTrace();
		}

		try{
			file.createNewFile();
		}catch(Exception e){
			e.printStackTrace();
		}

		OutputStream out=new FileOutputStream(file);
		bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        
    }


    public int[] getScans(){
    	int[] st=null; 
    	Cursor r=dquery(" select num from scans order by num desc");
    	st=new int[r.getCount()];
    	for(int i=0;i<st.length&&r.moveToNext();i++)
    		st[i]=r.getInt(0);

    	return st;
    } 


    public Scan getScan(int num){
    	Scan me=new Scan(ctx);
    	Cursor r=dquery("select * from scans where num='"+num+"'");
    	if(r.moveToNext()){
    		me.num=r.getInt(0);
    		me.value=r.getString(1);
    		me.type=r.getString(2);
    	}


    	return me;
    }








}

