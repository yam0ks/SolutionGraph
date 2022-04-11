package com.solutiongraph.restrictions;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.model.simplexdata.Restriction;
import com.solutiongraph.coeffs.CoeffAdapter;
import com.solutiongraph.R;
import com.utils.Constants;
import com.utils.Parsers;

public class RestrictViewHolder extends RecyclerView.ViewHolder {
    private final View root;
    private final View header;
    private final RadioGroup signView;
    private final EditText resultView;
    private final EditText freeCoeffView;
    private final ScrollView scrollView;
    private final RecyclerView coeffsView;
    private boolean toggle = false;
    private int errorCount = 0;

    public RestrictViewHolder(@NonNull View itemView) {
        super(itemView);
        this.root = itemView;
        this.header = itemView.findViewById(R.id.restrict_header);
        this.signView = itemView.findViewById(R.id.sign);
        this.resultView = itemView.findViewById(R.id.result);
        this.freeCoeffView = itemView.findViewById(R.id.free_coeff);
        this.scrollView = itemView.findViewById(R.id.scroll_view);
        this.coeffsView = root.findViewById(R.id.coeff_recyclerview);

        header.setOnClickListener(view -> {
            scrollView.setVisibility(toggle ? View.GONE : View.VISIBLE);
            toggle = !toggle;
            view.findViewById(R.id.expression_expand_arrow).setRotation(toggle ? 180 : 0);
        });

        //TODO: Отслеживание изменения фокуса
        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
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
            updateHeader();
        };
        resultView.setOnFocusChangeListener(focusChangeListener);
        freeCoeffView.setOnFocusChangeListener(focusChangeListener);
    }

    public void setHeaderText(Restriction restriction) {
        String text = Parsers.parseXmlFromRestriction(restriction);
        TextView textView = header.findViewById(R.id.expression_title);
        textView.setText(Html.fromHtml(text));
    }

    public void updateHeader() {
        if (errorCount != 0) return;
        double[] coeffs = ((CoeffAdapter)coeffsView.getAdapter()).getCoeffs();
        double freeCoeff = Double.parseDouble(freeCoeffView.getText().toString());
        double result = Double.parseDouble(resultView.getText().toString());

        Restriction restriction = new Restriction(coeffs, freeCoeff, Constants.Sign.EQUALS, result);
        setHeaderText(restriction);
    }

    public void setCoeffs(double[] coeffs) {
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
        this.resultView.setText(Parsers.stringFromNumber(newValue));
    }

    public void setFreeCoeff(double newValue) {
        this.freeCoeffView.setText(Parsers.stringFromNumber(newValue));
    }
}
