package com.solutiongraph.my_recyclerview_adapter.coeffs;

import com.utils.Parsers;

import android.view.Gravity;
import android.view.View;
import com.solutiongraph.R;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.solutiongraph.my_recyclerview_adapter.BaseRecyclerViewAdapter;

public class CoeffAdapter extends BaseRecyclerViewAdapter<String, CoeffViewHolder> {
    @FunctionalInterface
    public interface Procedure {
        void run();
    }

    private int index = 0;
    private final Procedure update;

    public CoeffAdapter(Context context, Double[] coeffs, Procedure update) {
        super(LayoutInflater.from(context), Parsers.convertDoubleSToStrings(coeffs));
        this.update = update;
    }

    @NonNull
    @Override
    public CoeffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View coeffViewItem = layoutInflater.inflate(R.layout.coeff_item, parent, false);

        return new CoeffViewHolder(coeffViewItem, data[index], index++, this);
    }

    public void updateCoeffs() {
        update.run();
    }

    private boolean checkNotZeroCoeff() {
        for (double item : Parsers.convertStringsToDoubles(getData())) {
            if (item != 0) return true;
        }
        return false;
    }

    @Override
    public boolean hasErrors() {
        if (super.hasErrors()) return true;
        if (!checkNotZeroCoeff()) {
            Toast zeroCoeffsError = Toast.makeText(this.layoutInflater.getContext(),
                    "Хотя бы один коэффициент должен быть не равен нулю", Toast.LENGTH_LONG);
            zeroCoeffsError.setGravity(Gravity.TOP, 0, 0);
            zeroCoeffsError.show();
            return true;
        }
        return false;
    }
}
