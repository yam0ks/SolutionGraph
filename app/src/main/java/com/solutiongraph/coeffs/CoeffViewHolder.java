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

public class CoeffViewHolder extends RecyclerView.ViewHolder {
    private final TextView coeffDesc;
    private final EditText coeff;
    private int errorCount = 0;

    public CoeffViewHolder(
            @NonNull View itemView,
            double coeffValue,
            int index,
            Function2<Integer, String, Boolean> onBlur)
    {
        super(itemView);

        this.coeffDesc = itemView.findViewById(R.id.coeff_description);
        this.coeff = itemView.findViewById(R.id.coeff);
        setIndex(index);
        setCoeff(coeffValue);

        coeff.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) return;
            String text = ((EditText)view).getText().toString();
            try {
                Double.parseDouble(text);
                view.setBackgroundColor(Color.argb(0, 1, 1,1));
                if (errorCount > 0) errorCount--;
            } catch (Exception e) {
                view.setBackgroundColor(Color.parseColor(Constants.ERROR_COLOR));
                errorCount++;
                return;
            }
            if (errorCount > 0) return;
            onBlur.apply(index, coeff.getText().toString());
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
