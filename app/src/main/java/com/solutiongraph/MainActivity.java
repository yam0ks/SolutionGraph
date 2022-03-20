package com.solutiongraph;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.Bundle;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    Activities activities = Activities.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activities.Get());

        SetSpinnerRange(R.id.variablesCountSpin, 11);
        SetSpinnerRange(R.id.restrictionsCountSpin, 11);
    }

    private void SetSpinnerRange(int spinnerID, Integer max) {
        String[] count = new String[max];
        for (int i = 0; i < max; i++) {
            count[i] = String.valueOf(i + 1);
        }
        Spinner spinner = findViewById(spinnerID);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, count);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    public void NextStep(View view) {
        if (!activities.Next()) return;
        setContentView(activities.Get());
    }
    public void PrevStep(View view) {
        if (!activities.Prev()) return;
        setContentView(activities.Get());
    }
}