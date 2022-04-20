package com.solutiongraph.my_recyclerview_adapter.coeffs;

import com.utils.Parsers;
import android.view.View;
import com.solutiongraph.R;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import com.solutiongraph.my_recyclerview_adapter.BaseRecyclerViewAdapter;

public class CoeffAdapter extends BaseRecyclerViewAdapter<Double, CoeffViewHolder> {
    @FunctionalInterface
    public interface Procedure {
        void run();
    }

    private int index = 0;
    private final Procedure update;

    public CoeffAdapter(Context context, double[] coeffs, Procedure update) {
        super(LayoutInflater.from(context), Parsers.doublePrimitiveArrayToDoubleArray(coeffs));
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
}
