package com.solutiongraph.restrictions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.model.Fraction;
import com.model.simplexdata.Restriction;
import com.solutiongraph.R;
import com.utils.Constants;

import java.util.Arrays;

public class RestrictAdapter extends RecyclerView.Adapter<RestrictViewHolder> {
    private final LayoutInflater layoutInflater;
    private Restriction[] restrictions;
    public int index = 0;
    public boolean[] hasErrors;

    public RestrictAdapter(Context context, Restriction[] data) {
        this.restrictions = data;
        this.layoutInflater = LayoutInflater.from(context);
        initErrorStatus();
    }

    @NonNull
    @Override
    public RestrictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View restrictViewItem =
                layoutInflater.inflate(R.layout.restrict_item, parent, false);

        return new RestrictViewHolder(restrictViewItem, this, index++);
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
        switch (sign) {
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

    private void initErrorStatus() {
        this.hasErrors = new boolean[restrictions.length];
        Arrays.fill(hasErrors , false);
    }

    public boolean restrictsHaveErrors() {
        for (boolean item : hasErrors) {
            if (item) return true;
        }
        return false;
    }

    public Restriction[] getRestrictions() {
        return restrictions;
    }

    public Restriction getRestrictionByIndex(int index) {
        return restrictions[index];
    }

    public void setRestrictions(Restriction[] restrictions) {
        this.restrictions = restrictions;
    }

    public void setRestrictionCoeffByIndex(int restrictIndex, int coeffIndex, double newCoeff) {
        this.restrictions[restrictIndex].setCoeff(coeffIndex, newCoeff);
    }

    public void setSign(int restrictIndex, Constants.Sign sign) {
        this.restrictions[restrictIndex].setSign(sign);
    }

    public void setFreeCoeff(int restrictIndex, double newValue) {
        this.restrictions[restrictIndex].setFreeCoeff(new Fraction(newValue));
    }

    public void setResult(int restrictIndex, double newValue) {
        this.restrictions[restrictIndex].setResult(new Fraction(newValue));
    }

    public void setRestrictionByIndex(int index, Restriction newValue) {
        this.restrictions[index] = newValue;
    }


    @Override
    public int getItemCount() {
        return this.restrictions.length;
    }
}
