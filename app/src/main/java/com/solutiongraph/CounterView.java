package com.solutiongraph;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CounterView extends Fragment {

    private static final String MAX_RESTRICTIONS_NUMBER = "max_rest";
    private static final String MAX_VARIABLES_NUMBER = "max_var";

    private int maxRest;
    private int maxVar;

    private View root;

    public CounterView() {}

    public static CounterView newInstance(int maxRest, int maxVar) {
        CounterView fragment = new CounterView();
        Bundle args = new Bundle();
        args.putInt(MAX_RESTRICTIONS_NUMBER, maxRest);
        args.putInt(MAX_VARIABLES_NUMBER, maxVar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            maxRest = getArguments().getInt(MAX_RESTRICTIONS_NUMBER);
            maxVar = getArguments().getInt(MAX_VARIABLES_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_counter_view, container, false);
        return inflater.inflate(R.layout.fragment_counter_view, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        Spinner spinner = root.findViewById(R.id.restrictionsCountSpin);
        SetSpinnerRange(root, spinner, maxRest);
        spinner = root.findViewById(R.id.variablesCountSpin);
        SetSpinnerRange(root, spinner, maxVar);
    }

    private void SetSpinnerRange(View view, Spinner spinner, Integer max) {
        String[] count = new String[max];
        for (int i = 0; i < max; i++) {
            count[i] = String.valueOf(i + 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(view.getContext(),
                android.R.layout.simple_spinner_item, count);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}