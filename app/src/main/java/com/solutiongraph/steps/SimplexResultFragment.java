package com.solutiongraph.steps;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.model.simplexdata.MatrixItem;
import com.model.simplexdata.Section;
import com.solutiongraph.R;
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

        if (getArguments() != null) {
            viewModel.sections.observe(this.getViewLifecycleOwner(), this::drawSections);
        }
    }

    private void drawSections(Section[] sections) {
        LinearLayout simplexResultLayout = root.findViewById(R.id.simplex_result_layout);
        simplexResultLayout.removeAllViews();
        for (Section section : sections) {
            simplexResultLayout.addView(drawTitle(section.title));
            simplexResultLayout.addView(drawDescription(section.description));
            simplexResultLayout.addView(drawMatrix(section.matrix));
        }
    }

    private TextView drawDescription(String description) {
        TextView descriptionView = new TextView(this.getContext());
        int blackColor = getResources().getColor(R.color.black);
        descriptionView.setTextColor(blackColor);
        descriptionView.setPadding(5, 5, 5, 5);
        descriptionView.setText(description);
        return descriptionView;
    }

    private TextView drawTitle(String title) {
        TextView titleView = drawDescription(title);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        return titleView;
    }

    @SuppressLint("DefaultLocale")
    private GridLayout drawMatrix(MatrixItem[][] matrix) {
        GridLayout matrixLayout = new GridLayout(this.getContext());
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.height = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(1, 1, 1, 1);
        matrixLayout.setLayoutParams(layoutParams);
        matrixLayout.setRowCount(matrix.length);
        matrixLayout.setColumnCount(matrix[0].length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                MatrixItem matrixItem = matrix[i][j];

                GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
                GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(rowSpan, colSpan);

                String matrixText = String.format("%d:%d", i, j);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Заглушка пока нет связи с ViewModel
        MatrixItem m = new MatrixItem();
        MatrixItem h = new MatrixItem();
        h.isHeader = true;
        this.root = inflater.inflate(R.layout.fragment_simplex_result, container, false);
        MatrixItem[][] matrixItems = new MatrixItem[][] {
                { h, m, m, h, m, m, m, h, h, h },
                { h, m, m, h, m, m, m, m, m, m}
        };
        Section mock = new Section("Заголовок", "Описание описание описание", matrixItems);
        Section[] mocks = new Section[] {mock};
        drawSections(mocks);
        return root;
    }
}