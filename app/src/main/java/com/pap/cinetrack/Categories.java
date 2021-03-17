package com.pap.cinetrack;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Categories extends  AppCompatActivity implements View.OnClickListener{

        private Button b1,b2,b3,b4;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.categories);

            b1=findViewById(R.id.but1);
            b2=findViewById(R.id.but2);
            b3=findViewById(R.id.but3);
            b4=findViewById(R.id.but4);
            b1.setOnClickListener(this);
            b2.setOnClickListener(this);
            b3.setOnClickListener(this);
            b4.setOnClickListener(this);

            GetInternetInfo(); //dedomena internet

        }

        private void GetInternetInfo(){
            ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkinfo = con.getActiveNetworkInfo(); //pairnei ta dedomena gia to internet
            if(networkinfo == null || !networkinfo.isConnected() || !networkinfo.isAvailable()) {

                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.alert_network_connection);

                dialog.setCanceledOnTouchOutside(false);

                dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

                Button btTryAgain = dialog.findViewById(R.id.retry);

                btTryAgain.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        recreate();
                    }
                });

                dialog.show();
            }
        }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.but1:
                    startActivity(new Intent(Categories.this, PaizetaiTwra.class));
                    break;
                case R.id.but2:
                    startActivity(new Intent(Categories.this,New_Movies.class));
                    break;
                case R.id.but3:
                    break;
                case R.id.but4:
                    break;
            }

    }

    }

