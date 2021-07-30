/*
    loading activity : 약 2초간 Loading 화면을 띄워준다.
    handler :
 */
package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class LoadingActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startLoading();

    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }


}