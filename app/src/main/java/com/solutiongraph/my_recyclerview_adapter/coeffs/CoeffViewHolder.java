package com.solutiongraph.my_recyclerview_adapter.coeffs;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    private final EditText coeff;

    public CoeffViewHolder(
            @NonNull View itemView,
            String coeffValue,
            int index,
            CoeffAdapter coeffAdapter
    ) {
        super(itemView);
        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        this.coeff = itemView.findViewById(R.id.coeff);
        setIndex(index);
        setCoeff(coeffValue);

        coeff.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) return;
            Drawable drawable;
            String coeff = getCoeff();
            coeffAdapter.setDataByIndex(index, coeff);
            try {
                Double.parseDouble(coeff);
                coeffAdapter.setErrorByIndex(index, false);
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.text_line);
            } catch (Exception e) {
                coeffAdapter.setErrorByIndex(index, true);
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.error_text_line);
            }
            view.setBackground(drawable);
            coeffAdapter.updateCoeffs();
        });
    }

    @SuppressLint("DefaultLocale")
    private void setIndex(int index) {
        String template = "Коэффициент при x<sub><small>%d</small></sub> =";
        coeffDesc.setText(Html.fromHtml(String.format(template, index + 1)));
    }

    public void setCoeff(String newValue) {
        if (newValue.equals("1")) return;
        coeff.setText(newValue);
    }

    public String getCoeff() {
        String result = coeff.getText().toString();
        return result.isEmpty() ? "1" : result;
    }
}
