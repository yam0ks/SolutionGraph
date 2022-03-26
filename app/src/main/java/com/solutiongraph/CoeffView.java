package com.solutiongraph;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CoeffView extends Fragment {
    public static final String RESTRICTIONS_NUMBER = "restrictions_number";
    public static final String VARIABLES_NUMBER = "variables_number";

    private int restNumber;
    private int varblNumber;

    public CoeffView() {
        // Required empty public constructor
    }

    public static CoeffView newInstance(int restNumber, int varblNumber) {
        CoeffView fragment = new CoeffView();
        Bundle args = new Bundle();
        args.putInt(RESTRICTIONS_NUMBER, restNumber);
        args.putInt(VARIABLES_NUMBER, varblNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restNumber = getArguments().getInt(RESTRICTIONS_NUMBER);
            varblNumber = getArguments().getInt(VARIABLES_NUMBER);
        }
//        LinearLayout rootContainer = getView().findViewById(R.id.root_container);

        if (savedInstanceState == null) {
            for (int i = 0; i < restNumber; i++) {
//                getSupportFragmentManager().beginTransaction()
//                        .setReorderingAllowed(true)
//                        .add(R.id.fragment_container_view, FragmentDropdownList.class, new Bundle(i))
//                        .commit();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coeff_view, container, false);
    }
}