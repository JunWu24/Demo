package com.junwu.demo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModelProviders;

import com.junwu.demo.R;
import com.junwu.demo.Utils.PaiAppLog;
import com.junwu.demo.viewmodel.LifeCycleViewModel;

/**
 * 1、在build.gradle中配置：implementation "android.arch.lifecycle:extensions:1.1.1"
 * 2、api 26以后，Activity和Fragment已经默认实现了LifecycleOwner接口，可直接通过getLifecycle获取Lifecycle对象
 * 3、可通过LifecycleRegistry实现自定义LifecycleOwner
 * 4、livedata使用：ViewModelProviders.of(this).get(Class<T>);
 */
public class LifeCycleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        getLifecycle().addObserver(new MyObserver());
        LifeCycleViewModel model = ViewModelProviders.of(this).get(LifeCycleViewModel.class);
        model.getName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                PaiAppLog.d("s : " + s);
            }
        });

    }

    class MyObserver implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume() {
            PaiAppLog.d("Lifecycle call onResume");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause() {
            PaiAppLog.d("Lifecycle call onPause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PaiAppLog.d("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PaiAppLog.d("onPause");
    }
}
