package com.solutiongraph;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.model.simplexdata.Restriction;
import com.utils.Constants;

import java.util.List;

public class RestrictAdapter extends RecyclerView.Adapter<RestrictViewHolder> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Restriction> restrictionList;
    private final int numbersCount;

    public RestrictAdapter(Context context, List<Restriction> data, int numbersCount) {
        this.context = context;
        this.restrictionList = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.numbersCount = numbersCount;
    }

    @NonNull
    @Override
    public RestrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View restrictViewItem = layoutInflater.inflate(R.layout.restrict_item, parent, false);

        return new RestrictViewHolder(restrictViewItem, numbersCount);
    }

    @Override
    public void onBindViewHolder(@NonNull RestrictViewHolder holder, int position) {
        Restriction restrict = this.restrictionList.get(position);
        double[] coeffs = restrict.getDoubleCoeffs();
        double freeCoeff = restrict.getFreeCoeffAsDouble();
        double result = restrict.getResultCoeffAsDouble();
        Constants.Sign sign = restrict.getSign();

        holder.result.setText(String.valueOf(result));
        holder.freeCoeffView.setText(String.valueOf(freeCoeff));
        switch (sign.name()){
            case ("LESS"):
                holder.signView.check(R.id.radio_less);
                break;
            case ("EQUAL"):
                holder.signView.check(R.id.radio_equal);
                break;
            case ("MORE"):
                holder.signView.check(R.id.radio_more);
                break;
        }
        holder.setHeader(coeffs, freeCoeff, sign, result);
    }

    @Override
    public int getItemCount() {
        return this.restrictionList.size();
    }
}
