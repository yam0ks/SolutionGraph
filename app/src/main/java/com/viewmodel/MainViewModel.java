package com.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Bundle;
import com.model.MainFunc;
import com.model.Restriction;
import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<Restriction>> restrictsMutable;
    public LiveData<List<Restriction>> restricts = restrictsMutable;

    private MutableLiveData<MainFunc> mainFunctionMutable;
    public LiveData<MainFunc> mainFunction = mainFunctionMutable;

}
