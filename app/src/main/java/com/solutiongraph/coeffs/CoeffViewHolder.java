package com.solutiongraph.coeffs;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;
import com.utils.Parsers;

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    private EditText coeff;
    public CoeffViewHolder(@NonNull View itemView, double coeff, int index) {
        super(itemView);

        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        this.coeff = itemView.findViewById(R.id.coeff);
        setIndex(index);
        setCoeff(coeff);
    }

    @SuppressLint("DefaultLocale")
    private void setIndex(int index) {
        String template = "Коэффициент при x<sub><small>%d</small></sub> =";
        coeffDesc.setText(Html.fromHtml(String.format(template, index + 1)));
    }

    public void setCoeff(double newValue) {
        coeff.setText(Parsers.stringFromNumber(newValue));
    }
}
