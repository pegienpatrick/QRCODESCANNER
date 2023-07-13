package com.qrcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class Results extends AppCompatActivity {
    private Scan myscan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Database mdb=new Database(this);
        int num=getIntent().getExtras().getInt("barcode");
        myscan=mdb.getScan(num);
        config();
    }



    private void config(){

        ((TextView)findViewById(R.id.content)).setText(myscan.value);
        if(myscan.type.equals("link"))
        {
            ((Button)findViewById(R.id.open)).setText("Open In Browser");
            ((Button)findViewById(R.id.open)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse((myscan.value.contains("http")?"":"https://")+myscan.value));
                        startActivity(i);
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        } else if(myscan.type.equals("email"))
        {
            ((Button)findViewById(R.id.open)).setText("Go to Email");
            ((Button)findViewById(R.id.open)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    emailIntent.setType("vnd.android.cursor.item/email");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {myscan.value});

                    startActivity(Intent.createChooser(emailIntent, "open email using..."));
                }
            });

        }
        else
        {
            ((Button)findViewById(R.id.open)).setVisibility(View.INVISIBLE);
        }

        ((Button)findViewById(R.id.copy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context=getApplicationContext();
                String text=myscan.value;
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(context,"Copied to Clipboard",Toast.LENGTH_LONG).show();

            }
            });

        try{
            ((ImageView)findViewById(R.id.picture)).setImageBitmap(myscan.getBmp());

        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
