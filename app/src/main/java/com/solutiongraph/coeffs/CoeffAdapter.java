package com.solutiongraph.coeffs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;


public class CoeffAdapter extends RecyclerView.Adapter<CoeffViewHolder> {
    int index = 0;
    private final double[] coeff;
    private final LayoutInflater layoutInflater;

    public CoeffAdapter(Context context, double[] coeffs) {
        this.coeff = coeffs;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CoeffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View coeffViewItem = layoutInflater.inflate(R.layout.coeff_item, parent, false);

        return new CoeffViewHolder(coeffViewItem, coeff[index], index++);
    }

    @Override
    public void onBindViewHolder(@NonNull CoeffViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.coeff.length;
    }
}
