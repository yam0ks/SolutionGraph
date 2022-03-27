package com.solutiongraph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.os.Bundle;

import com.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ViewModel vModel;
    private static final Stepper STEPPER = new Stepper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        vModel = new ViewModelProvider(this).get(MainViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCurrentFragment(STEPPER.getStep());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container_view, fragment, "MAIN_FRAGMENT")
                .commit();
    }

    public void NextStep(View view) {
        if (!STEPPER.Next()) return;
        if (!STEPPER.isFirst())
            findViewById(R.id.PrevButton).setVisibility(View.VISIBLE);

        switch (STEPPER.getIndex()) {
            case Stepper.COUNTS:
                break;
            case Stepper.COEFFS:
                Bundle bundle = new Bundle();
                //TODO: Передать кол-во ограничений
                bundle.putInt(CoeffView.RESTRICTIONS_NUMBER, 3);
                //TODO: Передать кол-во переменных
                bundle.putInt(CoeffView.VARIABLES_NUMBER, 3);
                STEPPER.getStep().setArguments(bundle);
                break;
        }
        setCurrentFragment(STEPPER.getStep());
    }
    public void PrevStep(View view) {
        if (!STEPPER.Prev()) return;
        if (STEPPER.isFirst())
            findViewById(R.id.PrevButton).setVisibility(View.INVISIBLE);
        setCurrentFragment(STEPPER.getStep());
    }
}