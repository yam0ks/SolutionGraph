package com.solutiongraph;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.model.constants;

public class RestrictViewHolder extends RecyclerView.ViewHolder {
    private final View root;
    private final View header;
    public EditText freeCoeffView;
    public RadioGroup signView;
    public EditText result;
    public RecyclerView coeffsRecyclerView;
    public ScrollView scrollView;
    boolean toggle = false;

    public RestrictViewHolder(@NonNull View itemView, int numbersCount) {
        super(itemView);
        this.root = itemView;
        this.freeCoeffView = itemView.findViewById(R.id.freeCoeff);
        this.signView = itemView.findViewById(R.id.sign);
        this.result = itemView.findViewById(R.id.result);
        this.scrollView = itemView.findViewById(R.id.scrollView);
        this.header = itemView.findViewById(R.id.restrict_header);

        View.OnClickListener toggleRestrict = view -> {
            scrollView.setVisibility(toggle ? View.GONE : View.VISIBLE);
            toggle = !toggle;
            view.findViewById(R.id.expressionExpandArrow).setRotation(toggle ? 180 : 0);
        };

        View.OnFocusChangeListener toggleChange  = (view, hasFocus) -> {
            if (hasFocus) return;
            
        };
        header.setOnClickListener(toggleRestrict);
        createCoeffsRecyclerView(numbersCount);
    }

    private void createCoeffsRecyclerView(int numbersCount) {
        this.coeffsRecyclerView = root.findViewById(R.id.coeff_recyclerview);
        double[] temp = new double[numbersCount];
        for (int i = 0; i < numbersCount; i++)
            temp[i] = 1;

        coeffsRecyclerView.setAdapter(
                new CoeffAdapter(root.getContext(), temp));
        coeffsRecyclerView.setLayoutManager(
                new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @SuppressLint("DefaultLocale")
    public void setHeader(double[] coeffs, double free, constants.Sign sign, double result) {
        String text = "";
        String template = "x<sub><small>%d</small></sub>";
        for (int i = 0; i < coeffs.length; i++) {
            double coeff = coeffs[i];
            if (coeff > 0) {
                text = text.concat(i != 0 ? " + " : "")
                        .concat(stringFromNumber(coeff))
                        .concat(String.format(template, i+1));
            } else if (coeff < 0) {
                text = text.concat(" - ")
                        .concat(stringFromNumber(Math.abs(coeff)))
                        .concat(String.format(template, i+1));
            }
        }
        if (free != 0)
            text = text.concat(free < 0 ? " - " : " + ").concat(stringFromNumber(Math.abs(free)));
        switch (sign) {
            case LEQ:
                text = text.concat(" < ");
                break;
            case EQ:
                text = text.concat(" = ");
                break;
            case GEQ:
                text = text.concat(" > ");
                break;
        }
        text = text.concat(stringFromNumber(result));
        setHeaderText(text);
    }

    private String stringFromNumber(double number) {
        int round = (int) Math.round(number);
        if (number == round) {
            return String.valueOf(round);
        }
        return String.valueOf(number);
    }

    private void setHeaderText(String text) {
        TextView textView = (TextView)header.findViewById(R.id.expressionTitle);
        textView.setText(Html.fromHtml(text));
    }

}
