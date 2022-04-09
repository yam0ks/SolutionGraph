package com.solutiongraph.steps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.solutiongraph.R;

public class CounterViewFragment extends Fragment {

    public static final String MAX_RESTRICTIONS_NUMBER = "max_rest";
    public static final String MAX_VARIABLES_NUMBER = "max_var";

    private int maxRest;
    private String[] restArray;
    private void setMaxRest(int newValue) {
        maxRest = newValue;
        restArray = createStringArray(maxRest);
    }
    private int maxVar;
    private String[] varArray;
    private void setMaxVar(int newValue) {
        maxVar = newValue;
        varArray = createStringArray(maxVar);
    }

    private String[] createStringArray(int length) {
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = String.valueOf(i + 1);
        }
        return array;
    }

    private Spinner restSpinner;
    private Spinner valSpinner;
    private View root;

    public CounterViewFragment() {}

    public static CounterViewFragment newInstance(int maxRest, int maxVar) {
        CounterViewFragment fragment = new CounterViewFragment();
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
            setMaxRest(getArguments().getInt(MAX_RESTRICTIONS_NUMBER));
            setMaxVar(getArguments().getInt(MAX_VARIABLES_NUMBER));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_counter_view, container, false);

        restSpinner = root.findViewById(R.id.restrictions_count_spin);
        restSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        valSpinner = root.findViewById(R.id.variables_count_spin);
        valSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        SetSpinnerRange(root, restSpinner, restArray);
//        SetSpinnerRange(root, valSpinner, varArray);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread() {
            public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SetSpinnerRange(root, restSpinner, restArray);
                                SetSpinnerRange(root, valSpinner, varArray);
                            }
                        });

                    }
        }.start();
    }

    private void SetSpinnerRange(View view, Spinner spinner, String[] array) {
        ArrayAdapter<?> adapter = new ArrayAdapter(view.getContext(),
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}