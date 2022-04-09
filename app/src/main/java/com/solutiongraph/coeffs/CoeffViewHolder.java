package com.solutiongraph.coeffs;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    public CoeffViewHolder(@NonNull View itemView, int index) {
        super(itemView);

        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        setIndex(index);
    }

    @SuppressLint("DefaultLocale")
    public void setIndex(int index) {
        String template = "Коэффициент при x<sub><small>%d</small></sub> =";
        coeffDesc.setText(Html.fromHtml(String.format(template, index)));
    }
}
