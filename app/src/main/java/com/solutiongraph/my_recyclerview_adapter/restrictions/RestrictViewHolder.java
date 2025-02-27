package com.solutiongraph.my_recyclerview_adapter.restrictions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.model.simplexdata.Restriction;
import com.solutiongraph.my_recyclerview_adapter.coeffs.CoeffAdapter;
import com.solutiongraph.R;
import com.utils.Constants;
import com.utils.Parsers;

import java.util.Objects;

public class RestrictViewHolder extends RecyclerView.ViewHolder {
    private final View root;
    private final View header;
    private final ScrollView scrollView;
    private final RadioGroup signView;
    private final RecyclerView coeffsView;
    private final EditText freeCoeffView;
    private final EditText resultView;
    private CoeffAdapter coeffAdapter;

    private final RestrictAdapter parentAdapter;

    private boolean toggle = false;
    private boolean resultIsCorrect = true;
    private boolean freeCoeffIsCorrect = true;

    public RestrictViewHolder(@NonNull View itemView, RestrictAdapter parentAdapter) {
        super(itemView);
        this.root = itemView;
        this.header = itemView.findViewById(R.id.restrict_header);
        this.scrollView = itemView.findViewById(R.id.scroll_view);
        this.signView = itemView.findViewById(R.id.sign);
        this.coeffsView = itemView.findViewById(R.id.coeff_recyclerview);
        this.freeCoeffView = itemView.findViewById(R.id.free_coeff);
        this.resultView = itemView.findViewById(R.id.result);

        this.parentAdapter = parentAdapter;

        this.freeCoeffView.setNextFocusDownId(R.id.result);
        this.resultView.setNextFocusDownId(R.id.coeff_recyclerview);

        header.setOnClickListener(view -> {
            scrollView.setVisibility(toggle ? View.GONE : View.VISIBLE);
            toggle = !toggle;
            hideKeyboardFrom();
            view.findViewById(R.id.expression_expand_arrow).setRotation(toggle ? 180 : 0);
        });

        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            if (hasFocus) return;
            String text = ((EditText)view).getText().toString();
            Drawable drawable;
            try {
                if (!text.isEmpty()) Double.parseDouble(text);
                if (view.getId() == R.id.result) resultIsCorrect = true;
                if (view.getId() == R.id.free_coeff) freeCoeffIsCorrect = true;
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.text_line);
            } catch (Exception e) {
                if (view.getId() == R.id.result) resultIsCorrect = false;
                if (view.getId() == R.id.free_coeff) freeCoeffIsCorrect = false;
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.error_text_line);
            }
            view.setBackground(drawable);
            updateHeader();
        };

        signView.setOnCheckedChangeListener((view, checkedId) -> updateHeader());
        resultView.setOnFocusChangeListener(focusChangeListener);
        freeCoeffView.setOnFocusChangeListener(focusChangeListener);
    }

    private void hideKeyboardFrom() {
        InputMethodManager imm = (InputMethodManager) this.root.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    private void setHeaderText() {
        String text = Parsers.parseXmlFromRestriction(
                getCoeffsString(),
                getFreeCoeffString(),
                getSign(),
                getResultString()
        );
        TextView textView = header.findViewById(R.id.expression_title);
        textView.setText(Html.fromHtml(text));
    }

    private void setHeaderColor(int foreground, int background) {
        int contextForeground = foreground;
        int contextBackground = background;
        try {
            contextForeground = root.getResources().getColor(foreground);
        } catch (Exception ignored) {};
        try {
            contextBackground = root.getResources().getColor(background);
        } catch (Exception ignored) {};
        ((TextView)header.findViewById(R.id.expression_title)).setTextColor(contextForeground);
        ((TextView)header.findViewById(R.id.expression_expand_arrow)).setTextColor(contextForeground);
        header.setBackgroundColor(contextBackground);
    }

    private boolean validate() {
        if (!freeCoeffIsCorrect || !resultIsCorrect || coeffAdapter.hasErrors()) {
            setHeaderColor(Color.RED, R.color.error_red);
            this.parentAdapter.setErrorByIndex(getAdapterPosition(), true);
            return false;
        }
        setHeaderColor(R.color.main_blue, 0);
        this.parentAdapter.setErrorByIndex(getAdapterPosition(), false);
        return true;
    }

    public void updateHeader() {
        setHeaderText();
        if (!validate()) return;
        int index = getAdapterPosition();
        this.parentAdapter.setErrorByIndex(index, false);
        Restriction rest = new Restriction(getCoeffsDouble(), getFreeCoeff(), getSign(), getResult());
        this.parentAdapter.setDataByIndex(index, rest);
    }

    public void setCoeffs(Double[] coeffs) {
        coeffAdapter = new CoeffAdapter(root.getContext(), coeffs, this::updateHeader);
        coeffsView.setAdapter(coeffAdapter);
        coeffsView.setLayoutManager(
                new LinearLayoutManager(root.getContext(),
                        LinearLayoutManager.VERTICAL, false));
    }

    public void signCheck(int id) {
        signView.check(id);
    }

    public void signCheck(Constants.Sign sign) {
        switch (sign) {
            case LESS:
                signCheck(R.id.radio_less);
                break;
            case EQUALS:
                signCheck(R.id.radio_equal);
                break;
            case MORE:
                signCheck(R.id.radio_more);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public Constants.Sign getSign() {
        switch (signView.getCheckedRadioButtonId()) {
            case R.id.radio_equal:
            default:
                return Constants.Sign.EQUALS;
            case R.id.radio_less:
                return Constants.Sign.LESS;
            case R.id.radio_more:
                return Constants.Sign.MORE;
        }
    }

    public void setResult(double newValue) {
        if (newValue == 0) return;
        this.resultView.setText(Parsers.formatDoubleString(newValue));
    }

    public double getResult() {
        return Double.parseDouble(getResultString());
    }

    public String getResultString() {
        String result = resultView.getText().toString();
        return result.isEmpty() ? "0" : result;
    }

    public double getFreeCoeff() {
        String result = freeCoeffView.getText().toString();
        return result.isEmpty() ? 0 : Double.parseDouble(result);
    }

    public String getFreeCoeffString() {
        return freeCoeffView.getText().toString();
    }

    public void setFreeCoeff(double newValue) {
        if (newValue == 0) return;
        this.freeCoeffView.setText(Parsers.formatDoubleString(newValue));
    }

    public String[] getCoeffsString() {
        return ((CoeffAdapter) Objects.requireNonNull(coeffsView.getAdapter())).getData();
    }

    public Double[] getCoeffsDouble() {
        String[] data = getCoeffsString();
        return Parsers.convertStringsToDoubles(data);
    }
}
