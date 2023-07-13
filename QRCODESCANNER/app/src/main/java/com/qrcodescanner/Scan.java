package com.qrcodescanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class Scan{
	public int num;
	public String value;
	public String type;
	private Context ctx;

	Scan(Context ctx){
		this.ctx=ctx;
	}

	public Bitmap getBmp(){
		Bitmap bmp=null;
		try{
			File folder = ctx.getExternalFilesDir("QrCodeReader");

		        // Creating file with name gfg.txt
		    File file = new File(folder, "ImageFile"+num+".jpeg");
		    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            bmp = BitmapFactory.decodeStream(bufferedInputStream);

		}catch(Exception e){
			e.printStackTrace();
		}


		return bmp;
	}
}
