package com.qrcodescanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qrcodescanner.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    public static Activity act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        act=this;
        try {
            ((Button)findViewById(R.id.opencam)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, DoScan.class));
                }
            });
        }catch (Exception e){

        }

        try{
            addPrevious();

        }catch (Exception e){

        }



    }


    public static void disp(String text)
    {
        final String str=text;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, str, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void addPrevious(){
        Database mdb=new Database(this);
        Context ctx=this;
        for(int i:mdb.getScans())
        {
            Button btn=new Button(ctx);
            Scan myscan=mdb.getScan(i);
            btn.setText(myscan.value);


            try{
                Drawable d = new BitmapDrawable(getResources(), myscan.getBmp()) ;
                btn.setCompoundDrawables(d,null,null,null);

            }catch (Exception e){
                e.printStackTrace();
            }

            ((LinearLayout) findViewById(R.id.prevholder)).addView(btn);

        }


    }

}