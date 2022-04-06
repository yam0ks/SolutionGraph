package com.model;

import junit.framework.TestCase;

public class SimplexTest extends TestCase {

    public void testGetResult() {
        Restriction[] restrictions = new Restriction[3];
        restrictions[0] = new Restriction(new double[]{2, 12, 14}, 0, constants.Sign.LESS, 349);
        restrictions[1] = new Restriction(new double[]{4, 12, 15}, 0, constants.Sign.LESS, 264);
        restrictions[2] = new Restriction(new double[]{17, 17, 18}, 0, constants.Sign.LESS, 337);
        Objective objective = new Objective(new double[]{3, 6, 7}, constants.GoalType.MAXIMIZE);
        Simplex simplex = new Simplex();
        Simplex.OutputData outputData = simplex.getResult(restrictions, objective);
        double result = (double)outputData.answers[outputData.answers.length - 1].getNumerator() /
                (double)outputData.answers[outputData.answers.length - 1].getDenominator();
        assertEquals(128, result, 10);
    }
}