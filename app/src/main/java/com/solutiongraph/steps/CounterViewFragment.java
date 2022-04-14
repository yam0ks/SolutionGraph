package com.solutiongraph.steps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.solutiongraph.R;
import com.utils.Constants;

public class CounterViewFragment extends Fragment {

    public CounterViewFragment() {}

    public static final String MAX_RESTRICTIONS_NUMBER = "max_rest";
    public static final String MAX_VARIABLES_NUMBER = "max_var";

    private String[] restrictArray;
    public void setMaxRestrict(int newValue) {
        restrictArray = createStringArray(newValue);
    }

    private String[] numbArray;
    public void setMaxVar(int newValue) {
        numbArray = createStringArray(newValue);
    }

    private String[] createStringArray(int length) {
        String[] array = new String[length];
        for (int i = 0; i < length; i++) {
            array[i] = String.valueOf(i + 1);
        }
        return array;
    }

    private Spinner restSpinner;
    private Spinner numbSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setMaxRestrict(getArguments().getInt(MAX_RESTRICTIONS_NUMBER));
            setMaxVar(getArguments().getInt(MAX_VARIABLES_NUMBER));
        } else {
            setMaxRestrict(Constants.MAX_RESTRICTIONS_NUMBER);
            setMaxVar(Constants.MAX_VARIABLES_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_counter_view, container, false);

        restSpinner = root.findViewById(R.id.restrictions_count_spin);
        SetSpinnerRange(root, restSpinner, restrictArray);
        numbSpinner = root.findViewById(R.id.variables_count_spin);
        SetSpinnerRange(root, numbSpinner, numbArray);

        Button nextButton = root.findViewById(R.id.next_button);
        nextButton.setOnClickListener(view -> {
            int restCount = Integer.parseInt(restSpinner.getSelectedItem().toString());
            Bundle bundle = new Bundle();
            bundle.putInt(RestrictionsViewFragment.RESTRICTIONS_NUMBER, restCount);
            int numbCount = Integer.parseInt(numbSpinner.getSelectedItem().toString());
            bundle.putInt(RestrictionsViewFragment.VARIABLES_NUMBER, numbCount);
            Navigation.findNavController(view).navigate(R.id.next_action, bundle);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void SetSpinnerRange(View view, Spinner spinner, String[] array) {
        ArrayAdapter<?> adapter = new ArrayAdapter(view.getContext(),
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}