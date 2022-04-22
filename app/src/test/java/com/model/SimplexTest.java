package com.model;

import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;
import com.model.simplexdata.SimplexOutputData;
import com.usecase.Simplex;
import com.utils.Constants;

import junit.framework.TestCase;

public class SimplexTest extends TestCase {

    public void testGetResult() {
        Restriction[] restrictions = new Restriction[3];
        restrictions[0] = new Restriction(new Double[]{2.0, 12.0, 14.0}, 0.0, Constants.Sign.LESS, 349.0);
        restrictions[1] = new Restriction(new Double[]{4.0, 12.0, 15.0}, 0.0, Constants.Sign.LESS, 264.0);
        restrictions[2] = new Restriction(new Double[]{17.0, 17.0, 18.0}, 0.0, Constants.Sign.LESS, 337.0);
        Objective objective = new Objective(new Double[]{3.0, 6.0, 7.0}, 0.0, Constants.GoalType.MAXIMIZE);
        Simplex simplex = new Simplex();
        SimplexOutputData outputData = simplex.getResult(restrictions, objective);
        double result = (double)outputData.getAnswers()[outputData.getAnswers().length - 1].getNumerator() /
                (double)outputData.getAnswers()[outputData.getAnswers().length - 1].getDenominator();
        assertEquals(128, result, 10);
    }
}