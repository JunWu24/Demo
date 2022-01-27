package com.junwu.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.junwu.demo.activity.LifeCycleActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mLifecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLifecycle = findViewById(R.id.lifecycle);
        mLifecycle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lifecycle:
                Intent intent = new Intent(this, LifeCycleActivity.class);
                startActivity(intent);
                break;
        }
    }
}