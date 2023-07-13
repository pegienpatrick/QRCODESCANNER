package com.qrcodescanner.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.qrcodescanner.Database;
import com.qrcodescanner.DoScan;
import com.qrcodescanner.LoadImg;
import com.qrcodescanner.MainActivity;
import com.qrcodescanner.R;
import com.qrcodescanner.Results;
import com.qrcodescanner.Scan;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }
    private int index;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }
    private View root;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(index==3)
            root =inflater.inflate(R.layout.fragment_main, container, false);
        else if(index==2)
        {
            root =inflater.inflate(R.layout.prev, container, false);
            try {
                addPrevious();
            }catch (Exception e){}

        }
        else {
            root = inflater.inflate(R.layout.cam, container, false);
            try {
                ((Button) (getActivity()).findViewById(R.id.opencam)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), DoScan.class));
                    }
                });


                ((Button) (getActivity()).findViewById(R.id.opengalery)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getContext(), LoadImg.class));
                    }
                });

            }catch (Exception e){}
        }


        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.d(TAG, "onChanged: "+s );
                Toast.makeText(getContext(),s,Toast.LENGTH_SHORT);
                if(s.equals("Help"))
                {
                    //root = inflater.inflate(R.layout.fragment_main, container, false);
                    final TextView textView = root.findViewById(R.id.section_label);
                    textView.setText(Html.fromHtml("<html><center><h1>Help</h1><br><br>The app opens with three tabs namely:- <br><ul><li>Scan</li><li>Previous</li><li>Help</li><ul><br> The Scan Menu enables the user to scan either from camera or from a file. When the user selects scan from camera, the barcode is detected automatically and results displayed same as when the user selects a file.<br><br> The results are then saved for future reference. To find saved results, the user clicks on the Previous tab. Here the user is provided with a list of all recent searches to get the results for the search, the user clicks on the result and they can see that search result.   </html>"));
                }
                else if(s.equals("Scan"))
                {
                    try {
                        ((Button) (getActivity()).findViewById(R.id.opencam)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), DoScan.class));
                            }
                        });

                        ((Button) (getActivity()).findViewById(R.id.opengalery)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), LoadImg.class));
                            }
                        });

                    }catch (Exception e){}
                }
                else{
                    try {
                        addPrevious();
                    }catch (Exception e){}

                }


            }
        });


        return root;
    }

    public void addPrevious(){
        ((LinearLayout) getActivity().findViewById(R.id.prevholder)).removeAllViews();
        Database mdb=new Database(getContext());
        Context ctx=getContext();
        for(int i:mdb.getScans())
        {
            Button btn=new Button(ctx);
            Scan myscan=mdb.getScan(i);
            btn.setText(myscan.value);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), Results.class);
                    intent.putExtra("barcode", myscan.num);
                    startActivity(intent);
                }
            });
            try{
                Drawable d = new BitmapDrawable(getResources(), myscan.getBmp()) ;
                d.setBounds( 0, 0, 200,200);
                btn.setCompoundDrawables(d,null,null,null);

            }catch (Exception e){
                e.printStackTrace();
            }

            ((LinearLayout) getActivity().findViewById(R.id.prevholder)).addView(btn);

        }


    }


}