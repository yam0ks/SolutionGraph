package com.solutiongraph.steps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;
import com.solutiongraph.R;
import com.solutiongraph.restrictions.RestrictAdapter;
import com.viewmodel.SharedViewModel;

public class MainFuncViewFragment extends Fragment {
    public static final String VARIABLES_NUMBER = "variables_number";

    private int varblNumber;
    private SharedViewModel viewModel;

    public MainFuncViewFragment() {
        // Required empty public constructor
    }

    public static MainFuncViewFragment newInstance(int varblNumber) {
        MainFuncViewFragment fragment = new MainFuncViewFragment();
        Bundle args = new Bundle();
        args.putInt(VARIABLES_NUMBER, varblNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        if (getArguments() != null) {
            varblNumber = getArguments().getInt(VARIABLES_NUMBER);
            viewModel.createMainFuncData(varblNumber);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_func_view, container, false);
        if (varblNumber <= 2) {
            root.findViewById(R.id.graphCheckBox).setVisibility(View.VISIBLE);
        } else {
            root.findViewById(R.id.graphCheckBox).setVisibility(View.GONE);
        }

        Button nextButton = root.findViewById(R.id.next_button);
        nextButton.setOnClickListener(view -> {
            CheckBox graphOption = root.findViewById(R.id.graphCheckBox);
            int nextStep = (varblNumber < 2 && graphOption.isChecked())
                    ? R.id.action_mainFuncViewFragment_to_graphResultFragment
                    : R.id.action_mainFuncViewFragment_to_simplexResultFragment;
            Navigation.findNavController(view).navigate(nextStep);
        });

        return root;
    }
}