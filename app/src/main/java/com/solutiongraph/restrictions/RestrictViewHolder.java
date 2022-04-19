package com.solutiongraph.restrictions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.model.simplexdata.Restriction;
import com.solutiongraph.coeffs.CoeffAdapter;
import com.solutiongraph.R;
import com.utils.Constants;
import com.utils.Parsers;

import java.util.Arrays;
import java.util.Objects;

public class RestrictViewHolder extends RecyclerView.ViewHolder {
    private final View root;
    private final View header;
    private final RestrictAdapter parentAdapter;
    private final RadioGroup signView;
    private final EditText resultView;
    private final EditText freeCoeffView;
    private final ScrollView scrollView;
    private final RecyclerView coeffsView;
    private boolean toggle = false;
    private boolean resultIsCorrect = true;
    private boolean freeCoeffIsCorrect = true;
    private boolean[] coeffErrorMas;
    private final int index;

    public RestrictViewHolder(@NonNull View itemView, RestrictAdapter parentAdapter, int index) {
        super(itemView);
        this.root = itemView;
        this.header = itemView.findViewById(R.id.restrict_header);
        this.signView = itemView.findViewById(R.id.sign);
        this.resultView = itemView.findViewById(R.id.result);
        this.freeCoeffView = itemView.findViewById(R.id.free_coeff);
        this.scrollView = itemView.findViewById(R.id.scroll_view);
        this.coeffsView = root.findViewById(R.id.coeff_recyclerview);
        this.parentAdapter = parentAdapter;
        this.index = index;
        this.freeCoeffView.setNextFocusDownId(R.id.result);
        this.resultView.setNextFocusDownId(R.id.coeff_recyclerview);

        header.setOnClickListener(view -> {
            scrollView.setVisibility(toggle ? View.GONE : View.VISIBLE);
            toggle = !toggle;
            view.findViewById(R.id.expression_expand_arrow).setRotation(toggle ? 180 : 0);
        });

        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            if (hasFocus) return;
            String text = ((EditText)view).getText().toString();
            Drawable drawable;
            try {
                if (!text.isEmpty()) {
                    Double.parseDouble(text);
                }
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.text_line);
                if (view.getId() == R.id.free_coeff) freeCoeffIsCorrect = true;
                if (view.getId() == R.id.result) resultIsCorrect = true;
            } catch (Exception e) {
                if (view.getId() == R.id.free_coeff) freeCoeffIsCorrect = false;
                if (view.getId() == R.id.result) resultIsCorrect = false;
                drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.error_text_line);
            }
            view.setBackground(drawable);
            updateHeader();
        };

        RadioGroup.OnCheckedChangeListener checkedChangeListener = (view, checkedId) -> updateHeader();

        signView.setOnCheckedChangeListener(checkedChangeListener);
        resultView.setOnFocusChangeListener(focusChangeListener);
        freeCoeffView.setOnFocusChangeListener(focusChangeListener);
    }

    public void setHeaderText(Restriction restriction) {
        String text = Parsers.parseXmlFromRestriction(restriction);
        TextView textView = header.findViewById(R.id.expression_title);
        textView.setText(Html.fromHtml(text));
    }

    private void setHeaderColor(int foreground, int background) {
        int contextForeground = foreground;
        int contextBackground = background;
        try {
            contextForeground = root.getResources().getColor(foreground);
        } catch (Exception e) {};
        try {
            contextBackground = root.getResources().getColor(background);
        } catch (Exception e) {};
        ((TextView)header.findViewById(R.id.expression_title)).setTextColor(contextForeground);
        ((TextView)header.findViewById(R.id.expression_expand_arrow)).setTextColor(contextForeground);
        header.setBackgroundColor(contextBackground);
    }

    public void updateHeader() {
        if (!freeCoeffIsCorrect || !resultIsCorrect || checkForCoeffErrors()) {
            setHeaderColor(Color.RED, R.color.error_red);
            this.parentAdapter.hasErrors[index] = true;
            return;
        }
        this.parentAdapter.hasErrors[index] = false;
        Constants.Sign sign = getSign();
        double[] coeffs = ((CoeffAdapter) Objects.requireNonNull(coeffsView.getAdapter())).getCoeffs();
        Restriction restriction = new Restriction(coeffs, getFreeCoeff(), sign, getResult());
        this.parentAdapter.setRestrictionByIndex(this.index, restriction);
        setHeaderColor(R.color.main_blue, R.color.white);
        setHeaderText(restriction);
    }

    public void setCoeffs(double[] coeffs) {
        this.coeffErrorMas = new boolean[coeffs.length];
        Arrays.fill(coeffErrorMas, false);
        coeffsView.setAdapter(
                new CoeffAdapter(root.getContext(), coeffs, this));
        coeffsView.setLayoutManager(
                new LinearLayoutManager(root.getContext(),
                        LinearLayoutManager.VERTICAL, false));
    }

    public void signCheck(int id) {
        signView.check(id);
    }

    public void setResult(double newValue) {
        if (newValue == 0) {
            return;
        }
        this.resultView.setText(Parsers.stringFromNumber(newValue));
    }

    public double getResult() {
        String result = resultView.getText().toString();
        return result.isEmpty() ? 0 : Double.parseDouble(result);
    }

    public double getFreeCoeff() {
        String result = freeCoeffView.getText().toString();
        return result.isEmpty() ? 0 : Double.parseDouble(result);
    }


    public void setFreeCoeff(double newValue) {
        if (newValue == 0) {
            return;
        }
        this.freeCoeffView.setText(Parsers.stringFromNumber(newValue));
    }

    public boolean checkForCoeffErrors() {
        for (boolean item : coeffErrorMas) {
            if (item) return true;
        }
        double[] coeffs = ((CoeffAdapter) Objects.requireNonNull(coeffsView.getAdapter())).getCoeffs();
        for (double item : coeffs) {
            if (item != 0) return false;
        }
        Toast zeroCoeffsError = Toast.makeText(this.root.getContext(),
                "Хотя бы один коэффициент должен быть не равен нулю", Toast.LENGTH_LONG);
        zeroCoeffsError.setGravity(Gravity.TOP, 0, 0);
        zeroCoeffsError.show();
        return true;
    }

    public void manageCoeffError(int pos, boolean choice) {
        if (pos >= coeffErrorMas.length || pos < 0)
            throw new ArrayIndexOutOfBoundsException("Incorrect coeff position out of error massive length");
        coeffErrorMas[pos] = choice;
    }

    public Restriction getRestrictionData() {
        double freeCoeff = Double.parseDouble(this.freeCoeffView.getText().toString());
        double result = Double.parseDouble(this.resultView.getText().toString());
        double [] coeffs = ((CoeffAdapter) Objects.requireNonNull(this.coeffsView.getAdapter())).getCoeffs();
        Constants.Sign sign = getSign();
        return new Restriction(coeffs, freeCoeff, sign, result);
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

}
