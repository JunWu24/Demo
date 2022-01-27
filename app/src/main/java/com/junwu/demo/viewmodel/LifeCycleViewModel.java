package com.junwu.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LifeCycleViewModel extends ViewModel {

    private MutableLiveData<String> mName;

    public LiveData<String> getName() {
        if (mName == null) {
            mName = new MutableLiveData<>();
            addName();
        }
        return mName;
    }

    private void addName() {
        mName.setValue("test livedata.");
    }

}
