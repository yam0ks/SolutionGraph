package com.solutiongraph.steps;

import androidx.fragment.app.Fragment;

import com.solutiongraph.steps.RestrictionsViewFragment;
import com.solutiongraph.steps.CounterViewFragment;
import com.utils.Constants;

public final class Stepper {
    private int index = 0;
    private final Fragment[] STEPS = {
            CounterViewFragment.newInstance(Constants.MAX_RESTRICTIONS_NUMBER, Constants.MAX_VARIABLES_NUMBER),
            RestrictionsViewFragment.newInstance(0, 0)
    };

    public final static int COUNTS = 0;
    public final static int COEFFS = 1;

    public Stepper() {}

    public boolean Next() {
        if (index + 1 >= STEPS.length)
            return false;
        index++;
        return true;
    }
    public boolean Prev() {
        if (index - 1 < 0)
            return false;
        index--;
        return true;
    }
    public Fragment getStep() {
        return STEPS[index];
    }
    public int getIndex() { return index; }
    public boolean isFirst() { return index == 0; }
    public boolean isLast() { return index == STEPS.length - 1; }
}