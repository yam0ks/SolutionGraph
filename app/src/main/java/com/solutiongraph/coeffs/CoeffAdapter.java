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

import java.util.function.BiFunction;

public class CoeffAdapter extends RecyclerView.Adapter<CoeffViewHolder> {
    private int index = 0;
    private int errorCount = 0;
    private final double[] coeffs;
    private final LayoutInflater layoutInflater;
    private final RestrictViewHolder parentHolder;
    private boolean[] coeffErrorMas;

    public CoeffAdapter(Context context, double[] coeffs, RestrictViewHolder parentHolder) {
        this.coeffs = coeffs;
        this.layoutInflater = LayoutInflater.from(context);
        this.parentHolder = parentHolder;
        initCoeffErrorMas();
    }

    @NonNull
    @Override
    public CoeffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View coeffViewItem = layoutInflater.inflate(R.layout.coeff_item, parent, false);

        return new CoeffViewHolder(coeffViewItem, coeffs[index], index++, this, this::updateCoeffs);
    }

    private boolean updateCoeffs(int coeffIndex, double newValue) {
        coeffs[coeffIndex] = newValue;

        if (checkForCoeffErrors()) {
            return false;
        }
        parentHolder.updateHeader();
        return true;
    }

    public double[] getCoeffs() {
        return coeffs;
    }

    @Override
    public void onBindViewHolder(@NonNull CoeffViewHolder holder, int position) {

    }

    private void initCoeffErrorMas() {
        coeffErrorMas = new boolean[coeffs.length];
        for (int i = 0; i < coeffErrorMas.length; i++) {
            coeffErrorMas[i] = true;
        }
    }

    public void manageCoeffError(int pos, boolean choice) {
        if (pos >= coeffErrorMas.length)
            throw new ArrayIndexOutOfBoundsException("Incorrect coeff position out of error massive length");
        coeffErrorMas[pos] = choice;
    }

    private boolean checkForCoeffErrors() {
        for (boolean item : coeffErrorMas) {
            if (!item) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return this.coeffs.length;
    }
}
