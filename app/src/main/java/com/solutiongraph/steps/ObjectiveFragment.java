package com.solutiongraph.steps;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.model.simplexdata.Objective;
import com.solutiongraph.R;
import com.solutiongraph.my_recyclerview_adapter.coeffs.CoeffAdapter;
import com.utils.Constants;
import com.utils.Parsers;
import com.viewmodel.SharedViewModel;

import java.util.Objects;

public class ObjectiveFragment extends Fragment {
    public static final String VARIABLES_NUMBER = "variables_number";

    private int varblNumber;
    private View root;
    private RadioGroup goalView;
    private RecyclerView coeffRecyclerView;
    private EditText freeCoeffView;
    private SharedViewModel viewModel;
    private CoeffAdapter coeffAdapter;
    private boolean freeCoeffIsCorrect = true;
    private boolean hasErrors = false;

    public ObjectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;
        viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        if (getArguments() != null) {
            varblNumber = getArguments().getInt(VARIABLES_NUMBER);
            viewModel.createObjectiveData(varblNumber);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_objective_view, container, false);

        Objective objective = viewModel.objective.getValue();
        if (objective == null) {
            viewModel.createObjectiveData(varblNumber);
            objective = viewModel.objective.getValue();
        }

        goalView = root.findViewById(R.id.goal);
        goalCheck(objective.getGoalType());

        coeffRecyclerView = root.findViewById(R.id.coeff_recyclerview);
        coeffAdapter = new CoeffAdapter(this.getContext(), objective.getDoubleCoeffs(),
                this::updateHeader);
        coeffRecyclerView.setAdapter(coeffAdapter);
        coeffRecyclerView.setLayoutManager(new LinearLayoutManager(
                this.getContext(), LinearLayoutManager.VERTICAL, false)
        );
        freeCoeffView = root.findViewById(R.id.free_coeff);
        setFreeCoeff(objective.getDoubleFreeCoeff());

        if (varblNumber <= 2) {
            root.findViewById(R.id.graphCheckBox).setVisibility(View.VISIBLE);
        } else {
            root.findViewById(R.id.graphCheckBox).setVisibility(View.GONE);
        }

        Button nextButton = root.findViewById(R.id.next_button);
        nextButton.setOnClickListener(view -> {
            nextButton.setFocusable(true);
            nextButton.setFocusableInTouchMode(true);
            nextButton.requestFocus();
            if (!hasErrors) {
                CheckBox graphOption = root.findViewById(R.id.graphCheckBox);
                viewModel.saveObjective(getObjective());
                int nextStep;
                SharedViewModel.taskType task;
                if (graphOption.isChecked()) {
                    task = SharedViewModel.taskType.GRAPHICAL;
                    nextStep = R.id.action_mainFuncViewFragment_to_graphResultFragment;
                } else {
                    task = SharedViewModel.taskType.SIMPLEX;
                    nextStep = R.id.action_mainFuncViewFragment_to_simplexResultFragment;
                }
                viewModel.getSolution(task);
                Navigation.findNavController(view).navigate(nextStep);
            }
        });
        updateHeader();
        return root;
    }

    private Double[] getCoeffs() {
        return ((CoeffAdapter)Objects.requireNonNull(coeffRecyclerView.getAdapter())).getData();
    }

    private void setFreeCoeff(double newValue) {
        if (newValue == 0) return;
        freeCoeffView.setText(Parsers.stringFromNumber(newValue));
    }

    private double getFreeCoeff() {
        String text = freeCoeffView.getText().toString();
        return text.isEmpty() ? 0 : Double.parseDouble(text);
    }

    private void goalCheck(Constants.GoalType goalType) {
        switch (goalType) {
            case MAXIMIZE:
                goalView.check(R.id.radio_max);
                break;
            case MINIMIZE:
                goalView.check(R.id.radio_min);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private Constants.GoalType getGoal() {
        switch (goalView.getCheckedRadioButtonId()) {
            case R.id.radio_max:
            default:
                return Constants.GoalType.MAXIMIZE;
            case R.id.radio_min:
                return Constants.GoalType.MINIMIZE;
        }
    }

    private Objective getObjective() {
        return new Objective(getCoeffs(), getFreeCoeff(), getGoal());
    }

    private String[] getStringCoeffs() {
        CoeffAdapter coeffAdapter = (CoeffAdapter)Objects.requireNonNull(coeffRecyclerView.getAdapter());
        return coeffAdapter.getDataConvertedToString();
    }

    private String getStringFreeCoeff() {
        return freeCoeffView.getText().toString();
    }


    public void setHeaderText(String[] coeffs, String freeCoeff, Constants.GoalType goal) {
        // String text = Parsers.parseXmlFromObjective(coeffs, freeCoeff, goal);
        TextView textView = root.findViewById(R.id.expression_title);
        // textView.setText(Html.fromHtml(text));
    }

    private void setHeaderColor(int foreground, int background) {
        int contextForeground = foreground;
        int contextBackground = background;
        TextView headerText = (TextView)root.findViewById(R.id.expression_title);

        try {
            contextForeground = getResources().getColor(foreground);
        } catch (Exception e) {}
        headerText.setTextColor(contextForeground);

        try {
            Drawable drawable = ContextCompat.getDrawable(root.getContext(), background);
            headerText.setBackground(drawable);
            return;
        } catch (Exception e) {
            try {
                contextBackground = getResources().getColor(background);
            } catch (Exception e1) {}
        }
        headerText.setBackgroundColor(contextBackground);
    }

    private boolean validate() {
        if (!freeCoeffIsCorrect || coeffAdapter.hasErrors()) {
            setHeaderColor(Color.RED, R.color.error_red);
            hasErrors = true;
            return false;
        }
        setHeaderColor(R.color.main_blue, R.drawable.layout_lines);
        hasErrors = false;
        return true;
    }

    public void updateHeader() {
        validate();
        setHeaderText(getStringCoeffs(), getStringFreeCoeff(), getGoal());
    }
}