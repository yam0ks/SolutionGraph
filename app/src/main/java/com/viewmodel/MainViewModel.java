package com.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Bundle;

import com.model.Fraction;
import com.model.Restriction;
import java.util.List;
import com.model.GraphObjective;
import com.model.GraphRestriction;
import com.model.Objective;
import com.model.constants;
import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Restriction[]> restrictsMutable;
    public LiveData<Restriction[]> restricts = restrictsMutable;


    private MutableLiveData<Objective> mainFunctionMutable;
    public LiveData<Objective> mainFunction = mainFunctionMutable;


    public void GraphSolution(Bundle bundle){

    }

    public Object ConvertToGraphRestriction(Restriction[] restrictions){

        List<GraphRestriction> result = new ArrayList<>();

        for(Restriction restric : restrictions){
            double[] coeffs = restric.getDoubleCoeffs();

            if(coeffs.length != 2)
                return false;

            Float x_value = (float)coeffs[0];
            Float y_value = (float)coeffs[1];
            Float result_value = (float)restric.ResultCoeffAsDouble() - (float)restric.FreeCoeffAsDouble();
            constants.Sign sign = restric.sign;

            result.add(new GraphRestriction(x_value, y_value, sign, result_value));
        }

        return result;
    }

    public Object ConvertToGraphObjective(Objective objective){

        double[] coeffs = objective.getDoubleCoeffs();

        if(coeffs.length != 2)
            return false;

        Float x_value = (float)coeffs[0];
        Float y_value = (float)coeffs[1];
        constants.GoalType goal = objective.goal_type;

        return new GraphObjective(x_value, y_value, goal);
    }

    public void changeRestrictCoeffs(int restrictIndex, int coeffIndex, double newValue) {
        Restriction[] restrictsNormal = restrictsMutable.getValue();
        Fraction[] fracsNormal = restrictsNormal[restrictIndex].getFractionCoeffs();
        fracsNormal[coeffIndex] = new Fraction(newValue);
        restrictsNormal[restrictIndex].setFractionCoeffs(fracsNormal);
        restrictsMutable.setValue(restrictsNormal);
    }

    public void changeRestrictFreeCoeff(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].freeCoeff = new Fraction(newValue);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictRes(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].result = new Fraction(newValue);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictSign(int restrictIndex, constants.Sign newSign) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].sign = newSign;
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeMainFuncCoeff(int coeffIndex, double newValue) {
        Objective mainFunc = mainFunctionMutable.getValue();
        Fraction[] fracsNormal = mainFunc.getFractionCoeffs();
        fracsNormal[coeffIndex] = new Fraction(newValue);
        mainFunc.setFractionCoeffs(fracsNormal);
        mainFunctionMutable.setValue(mainFunc);
    }

    public void changeMainFuncGoalType(constants.GoalType goal) {
        Objective mainFunc = mainFunctionMutable.getValue();
        mainFunc.goal_type = goal;
        mainFunctionMutable.setValue(mainFunc);
    }

    public void createRestrictionData(int restrictionCount, int variableCount) {
        Restriction[] restrictionList = new Restriction[restrictionCount];
        double[] sampleCoeffs = new double[variableCount];
        for (int i = 0; i < variableCount; i++){
            sampleCoeffs[i] = 1;
        }
        for (int i = 0; i < restrictionCount; i++) {
            restrictionList[restrictionCount] = new Restriction(sampleCoeffs, 0, constants.Sign.EQUALS, 0);
        }
        restrictsMutable.setValue(restrictionList);
    }

    public void createMainFuncData(int variableCount) {
        double[] sampleCoeffs = new double[variableCount];
        for (int i = 0; i < variableCount; i++) {
            sampleCoeffs[i] = 1;
        }
        Objective mainFunc = new Objective(sampleCoeffs, constants.GoalType.MAXIMIZE);
        mainFunctionMutable.setValue(mainFunc);
    }



}
