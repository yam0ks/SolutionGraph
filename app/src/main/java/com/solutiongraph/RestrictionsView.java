package com.solutiongraph;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.model.Restriction;
import com.model.constants;

import java.util.ArrayList;
import java.util.List;

public class RestrictionsView extends Fragment {
    public static final String RESTRICTIONS_NUMBER = "restrictions_number";
    public static final String VARIABLES_NUMBER = "variables_number";

    private int restNumber;
    private int varblNumber;

    public RestrictionsView() {
        // Required empty public constructor
    }

    public static RestrictionsView newInstance(int restNumber, int varblNumber) {
        RestrictionsView fragment = new RestrictionsView();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_coeff_view, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Restriction> restrictionsList = createRestrictionsList();
        RecyclerView restrictionRecyclerView = this.requireView().findViewById(R.id.restrictionRecyclerView);
        restrictionRecyclerView.setAdapter(
                new RestrictAdapter(this.getContext(), restrictionsList, varblNumber));
        restrictionRecyclerView.setLayoutManager(
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @NonNull
    private List<Restriction> createRestrictionsList() {
        List<Restriction> restrictionList = new ArrayList<>();
        for (int i = 0; i < restNumber; i++) {
            restrictionList.add(
                    new Restriction(new double[varblNumber], 0, constants.Sign.MORE, 0));
        }
        return restrictionList;
    }
}