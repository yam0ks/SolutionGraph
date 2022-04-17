package com.solutiongraph.coeffs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;
import com.solutiongraph.restrictions.RestrictViewHolder;

public class CoeffAdapter extends RecyclerView.Adapter<CoeffViewHolder> {
    private int index = 0;
    private final double[] coeffs;
    private final LayoutInflater layoutInflater;
    public final RestrictViewHolder parentHolder;

    public CoeffAdapter(Context context, double[] coeffs, RestrictViewHolder parentHolder) {
        this.coeffs = coeffs;
        this.layoutInflater = LayoutInflater.from(context);
        this.parentHolder = parentHolder;
    }

    @NonNull
    @Override
    public CoeffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View coeffViewItem = layoutInflater.inflate(R.layout.coeff_item, parent, false);

        return new CoeffViewHolder(coeffViewItem, coeffs[index], index++, this, this::updateCoeffs);
    }

    private void updateCoeffs(int coeffIndex, double newValue) {
        coeffs[coeffIndex] = newValue;
    }

    public double[] getCoeffs() {
        return coeffs;
    }

    @Override
    public void onBindViewHolder(@NonNull CoeffViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.coeffs.length;
    }
}
