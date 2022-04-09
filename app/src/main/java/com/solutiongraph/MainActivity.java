package com.solutiongraph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.os.Bundle;

import com.solutiongraph.steps.RestrictionsViewFragment;
import com.solutiongraph.steps.Stepper;

public class MainActivity extends AppCompatActivity {
//    private MainViewModel mainViewModel;
    private static final Stepper STEPPER = new Stepper();
    int mainFragmentID = R.id.main_fragment_container_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                .replace(mainFragmentID, fragment, "MAIN_FRAGMENT")
                .commit();
    }

    @Override
    public void onBackPressed() {
        PrevStep();
    }

    public void nextStep(View view) {
        if (!STEPPER.Next()) return;

        Bundle bundle = new Bundle();
        switch (STEPPER.getIndex()) {
            case Stepper.COUNTS:
                break;
            case Stepper.COEFFS:
                //TODO: Передать кол-во ограничений
                bundle.putInt(RestrictionsViewFragment.RESTRICTIONS_NUMBER, 2);
                //TODO: Передать кол-во переменных
                bundle.putInt(RestrictionsViewFragment.VARIABLES_NUMBER, 4);
                STEPPER.getStep().setArguments(bundle);

                setCurrentFragment(STEPPER.getStep());

                break;
        }
        setCurrentFragment(STEPPER.getStep());
    }
    public void PrevStep() {
        if (!STEPPER.Prev()) return;
        setCurrentFragment(STEPPER.getStep());
    }
}