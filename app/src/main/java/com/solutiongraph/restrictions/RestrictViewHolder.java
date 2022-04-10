package com.solutiongraph.restrictions;

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
import com.utils.Parsers;

public class RestrictViewHolder extends RecyclerView.ViewHolder {
    private final View root;
    private final View header;
    private RadioGroup signView;
    private EditText result;
    private EditText freeCoeffView;
    private RecyclerView coeffsRecyclerView;
    private ScrollView scrollView;
    private boolean toggle = false;

    public RestrictViewHolder(@NonNull View itemView, int numbersCount) {
        super(itemView);
        this.root = itemView;
        this.freeCoeffView = itemView.findViewById(R.id.free_coeff);
        this.signView = itemView.findViewById(R.id.sign);
        this.result = itemView.findViewById(R.id.result);
        this.scrollView = itemView.findViewById(R.id.scroll_view);
        this.header = itemView.findViewById(R.id.restrict_header);

        View.OnClickListener toggleRestrict = view -> {
            scrollView.setVisibility(toggle ? View.GONE : View.VISIBLE);
            toggle = !toggle;
            view.findViewById(R.id.expression_expand_arrow).setRotation(toggle ? 180 : 0);
        };

        //TODO: Отслеживание изменения фокуса
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
                new LinearLayoutManager(root.getContext(),
                        LinearLayoutManager.VERTICAL, false));
    }

    public void setHeaderText(Restriction restriction) {
        String text = Parsers.parseXmlFromRestriction(restriction);
        TextView textView = header.findViewById(R.id.expression_title);
        textView.setText(Html.fromHtml(text));
    }

    public void signCheck(int id) {
        signView.check(id);
    }

    public void setResult(double newValue) {
        this.result.setText(Parsers.stringFromNumber(newValue));
    }

    public void setFreeCoeff(double newValue) {
        this.freeCoeffView.setText(Parsers.stringFromNumber(newValue));
    }
}
