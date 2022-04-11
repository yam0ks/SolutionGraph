package com.solutiongraph.coeffs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;
import com.solutiongraph.restrictions.RestrictViewHolder;
import com.utils.Constants;

public class CoeffAdapter extends RecyclerView.Adapter<CoeffViewHolder> {
    private int index = 0;
    private int errorCount = 0;
    private final double[] coeffs;
    private final LayoutInflater layoutInflater;
    private final RestrictViewHolder parentHolder;

    public CoeffAdapter(Context context, double[] coeffs, RestrictViewHolder parentHolder) {
        this.coeffs = coeffs;
        this.layoutInflater = LayoutInflater.from(context);
        this.parentHolder = parentHolder;
    }

    @NonNull
    @Override
    public CoeffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View coeffViewItem = layoutInflater.inflate(R.layout.coeff_item, parent, false);

        return new CoeffViewHolder(coeffViewItem, coeffs[index], index++, (coeffIndex, newValue) -> {
            coeffs[coeffIndex] = Double.parseDouble(newValue);
            parentHolder.updateHeader();
            return true;
        });
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
