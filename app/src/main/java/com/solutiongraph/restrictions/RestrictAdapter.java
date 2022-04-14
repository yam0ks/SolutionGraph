package com.solutiongraph.restrictions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.model.simplexdata.Restriction;
import com.solutiongraph.R;
import com.utils.Constants;

public class RestrictAdapter extends RecyclerView.Adapter<RestrictViewHolder> {
    private final LayoutInflater layoutInflater;
    private final Restriction[] restrictions;

    public RestrictAdapter(Context context, Restriction[] data) {
        this.restrictions = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RestrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View restrictViewItem =
                layoutInflater.inflate(R.layout.restrict_item, parent, false);

        return new RestrictViewHolder(restrictViewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RestrictViewHolder holder, int position) {
        Restriction restrict = this.restrictions[position];
        holder.setHeaderText(restrict);
        double[] coeffs = restrict.getDoubleCoeffs();
        double freeCoeff = restrict.getFreeCoeffAsDouble();
        double result = restrict.getResultAsDouble();
        Constants.Sign sign = restrict.getSign();
        holder.setCoeffs(coeffs);
        holder.setResult(result);
        holder.setFreeCoeff(freeCoeff);
        switch (sign){
            case LESS:
                holder.signCheck(R.id.radio_less);
                break;
            case EQUALS:
                holder.signCheck(R.id.radio_equal);
                break;
            case MORE:
                holder.signCheck(R.id.radio_more);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.restrictions.length;
    }
}
