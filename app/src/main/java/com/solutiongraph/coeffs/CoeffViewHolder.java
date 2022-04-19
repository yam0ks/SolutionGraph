package com.solutiongraph.coeffs;

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
import com.utils.Parsers;

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    private final EditText coeff;

    public CoeffViewHolder(
            @NonNull View itemView,
            double coeffValue,
            int index,
            CoeffAdapter coeffAdapter,
            Function2<Integer, Double> onBlur)
    {
        super(itemView);
        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        this.coeff = itemView.findViewById(R.id.coeff);
        setIndex(index);
        setCoeff(coeffValue);

        coeff.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) return;
            double newValue;
            Drawable drawable;
            try {
                newValue = getCoeff();
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.text_line);
                coeffAdapter.parentHolder.manageCoeffError(index, false);
                onBlur.apply(index, newValue);
            } catch (Exception e) {
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.error_text_line);
                coeffAdapter.parentHolder.manageCoeffError(index, true);
            }
            view.setBackground(drawable);
            coeffAdapter.parentHolder.updateHeader();
        });
    }

    @FunctionalInterface
    interface Function2<One, Two> {
        void apply(One one, Two two);
    }

    @SuppressLint("DefaultLocale")
    private void setIndex(int index) {
        String template = "Коэффициент при x<sub><small>%d</small></sub> =";
        coeffDesc.setText(Html.fromHtml(String.format(template, index + 1)));
    }

    public void setCoeff(double newValue) {
        if (newValue == 1.0) {
            return;
        }
        coeff.setText(Parsers.stringFromNumber(newValue));
    }

    public double getCoeff() {
        String result = coeff.getText().toString();
        return result.isEmpty() ? 1 : Double.parseDouble(result);
    }
}
