package com.solutiongraph.steps;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.graphdata.GraphOutputData;
import com.model.simplexdata.MatrixItem;
import com.model.simplexdata.Section;
import com.solutiongraph.R;
import com.utils.Constants;
import com.viewmodel.SharedViewModel;

public class SimplexResultFragment extends Fragment {

    private View root;
    private SharedViewModel viewModel;

    public SimplexResultFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) return;
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        final Observer<Section[]> sectionsObserver = sections -> {
            if(sections != null)
                drawSections(sections);
        };

        viewModel.sections.observe(this, sectionsObserver);
    }

    private void drawSections(Section[] sections) {
        LinearLayout simplexResultLayout = root.findViewById(R.id.simplex_result_layout);
        simplexResultLayout.removeAllViews();
        for (Section section : sections) {
            TextView textView = drawTitle(section.title);
            textView.setPadding(0, 32, 0, 5);
            simplexResultLayout.addView(textView);
            simplexResultLayout.addView(drawDescription(section.description));
            simplexResultLayout.addView(drawMatrix(section.matrix));
        }
    }

    private TextView drawDescription(String description) {
        TextView descriptionView = new TextView(this.getContext());
        int blackColor = getResources().getColor(R.color.black);
        descriptionView.setTextColor(blackColor);
        descriptionView.setPadding(5, 5, 5, 5);
        descriptionView.setText(Html.fromHtml(description));
        return descriptionView;
    }

    private TextView drawTitle(String title) {
        TextView titleView = drawDescription(title);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        return titleView;
    }

    @SuppressLint({"DefaultLocale", "UseCompatLoadingForDrawables"})
    private GridLayout drawMatrix(MatrixItem[][] matrix) {
        if (matrix == null) return new GridLayout(this.getContext());
        GridLayout matrixLayout = new GridLayout(this.getContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(1, 1, 1, 1);
        matrixLayout.setLayoutParams(layoutParams);
        matrixLayout.setRowCount(matrix.length);
        matrixLayout.setColumnCount(matrix[0].length);

        for (MatrixItem[] matrixItems : matrix) {
            for (MatrixItem matrixItem : matrixItems) {
                GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(rowSpan, colSpan);

                String matrixText = matrixItem.value;
                Spanned str = Html.fromHtml(matrixText);
                if (matrixText.length() > 7) {
                    matrixText = matrixText.substring(0, 7) + "...";
                }
                TextView matrixTextView = matrixItem.isHeader
                        ? drawTitle(matrixText) : drawDescription(matrixText);
                matrixTextView.setBackground(getResources().getDrawable(R.drawable.matrix_item_border));
                matrixTextView.setGravity(Gravity.CENTER);
                matrixLayout.addView(matrixTextView, gridParam);
            }
        }
        return matrixLayout;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.setSectionsMutableToNull();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_simplex_result, container, false);
        return root;
    }
}