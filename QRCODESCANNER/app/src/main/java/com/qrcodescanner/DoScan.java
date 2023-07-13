package com.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static androidx.constraintlayout.widget.StateSet.TAG;

public class DoScan extends AppCompatActivity {

    SurfaceView camView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;


    private static final int REQUEST_CAMERA_PERMISSION = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_scan);
        camView=findViewById(R.id.camview);
        camView.setDrawingCacheEnabled(true);
        ((Button)findViewById(R.id.btnAction)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoScan.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void initialiseDetectorsAndSources() {
        

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        camView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(DoScan.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(camView.getHolder());

                    } else {
                        ActivityCompat.requestPermissions(DoScan.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                camView.buildDrawingCache(true);
                saveBitmap=camView.getDrawingCache();
                Log.d(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    //Toast.makeText(getApplicationContext(), "Barcode : "+barcodes.valueAt(0).displayValue, Toast.LENGTH_SHORT).show();


                    String type="gen";
                    if(barcodes.valueAt(0).email!=null)
                        type="email";
                    else if(barcodes.valueAt(0).url!=null)
                        type="link";

                    Database mdb=new Database(getApplicationContext());

                    int res=mdb.addScan(barcodes.valueAt(0).displayValue,type,getFrom(camView));
                    if(res>0) {
                        Intent intent = new Intent(DoScan.this, Results.class);
                        intent.putExtra("barcode", res);
                        startActivity(intent);
                    }

                    
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    public static Bitmap saveBitmap;
    public Bitmap getFrom(View myview){
        //saveBitmap=null;
        //saveBitmap=getViewBitmap(myview);
        return saveBitmap;
    }

    Bitmap getViewBitmap(View view)
    {
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        int width = view.getWidth();
        int height =view.getHeight();

        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c);

        ((LinearLayout)findViewById(R.id.coke)).setDrawingCacheEnabled(true);

        ((LinearLayout)findViewById(R.id.coke)).buildDrawingCache(true);


        return ((LinearLayout)findViewById(R.id.coke)).getDrawingCache();
    }


   
}
