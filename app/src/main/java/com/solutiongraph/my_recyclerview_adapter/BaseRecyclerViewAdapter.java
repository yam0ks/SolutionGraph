package com.solutiongraph.my_recyclerview_adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

public class BaseRecyclerViewAdapter
        <Data, ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder>
{
    protected Data[] data;
    protected boolean[] errorsArray;
    protected final LayoutInflater layoutInflater;

    public BaseRecyclerViewAdapter(LayoutInflater layoutInflater, Data[] data) {
        this.layoutInflater = layoutInflater;
        this.data = data;
        this.errorsArray = new boolean[data.length];
        Arrays.fill(errorsArray, false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.data.length;
    }

    public void setDataByIndex(int index, Data newValue) {
        if (index < 0 || index >= data.length) return;
        data[index] = newValue;
    }

    public Data[] getData() {
        return data;
    }

    public void setErrorByIndex(int index, boolean newValue) {
        if (index < 0 || index >= errorsArray.length) return;
        errorsArray[index] = newValue;
    }

    public boolean hasErrors() {
        if (errorsArray.length == 0) return false;
        for (boolean error : errorsArray)
            if (error) return true;
        return false;
    }
}
