package com.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class LoadImg extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImage();
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try{
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                        Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                        Frame frame = new Frame.Builder().setBitmap(bmp).build();

                        BarcodeDetector detector= new BarcodeDetector.Builder(this)
                                .setBarcodeFormats(Barcode.ALL_FORMATS)
                                .build();
                        SparseArray<Barcode> barcodes = detector.detect(frame);

                        if (barcodes.size() != 0) {
                            //Toast.makeText(getApplicationContext(), "Barcode : "+barcodes.valueAt(0).displayValue, Toast.LENGTH_SHORT).show();
                            String type="gen";
                            if(barcodes.valueAt(0).email!=null)
                                type="email";
                            else if(barcodes.valueAt(0).url!=null)
                                type="link";

                            Database mdb=new Database(getApplicationContext());

                            int res=mdb.addScan(barcodes.valueAt(0).displayValue,type,bmp);
                            if(res>0) {
                                Intent intent = new Intent(LoadImg.this, Results.class);
                                intent.putExtra("barcode", res);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Failed to get Barcode from image", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoadImg.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
        }
    }
}
