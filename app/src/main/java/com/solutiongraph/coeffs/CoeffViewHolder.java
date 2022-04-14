package com.solutiongraph.coeffs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.solutiongraph.R;
import com.utils.Constants;
import com.utils.Parsers;

import java.util.function.BiFunction;

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    private final EditText coeff;
    private int errorCount = 0;
    private CoeffAdapter coeffAdapter;

    public CoeffViewHolder(
            @NonNull View itemView,
            double coeffValue,
            int index,
            CoeffAdapter coeffAdapter,
            Function2<Integer, Double, Boolean> onBlur)
    {
        super(itemView);
        this.coeffAdapter = coeffAdapter;
        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        this.coeff = itemView.findViewById(R.id.coeff);
        setIndex(index);
        setCoeff(coeffValue);

        coeff.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) return;
            double newValue;
            String text = ((EditText)view).getText().toString();
            try {
                newValue = Double.parseDouble(text);
                view.setBackgroundColor(Color.argb(0, 1, 1,1));
                coeffAdapter.manageCoeffError(index, true);
            } catch (Exception e) {
                view.setBackgroundColor(Color.parseColor(Constants.ERROR_COLOR));
                coeffAdapter.manageCoeffError(index, false);
                return;
            }
            //if (errorCount > 0) return;
            onBlur.apply(index, newValue);
        });
    }

    @FunctionalInterface
    interface Function2<One, Two, Return> {
        public Return apply(One one, Two two);
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
