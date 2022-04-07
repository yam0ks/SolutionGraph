package com.viewmodel;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;

import com.model.Fraction;
import com.model.GraphSolver;
import com.model.Model;
import com.model.Restriction;
import java.util.List;
import com.model.GraphObjective;
import com.model.GraphRestriction;
import com.model.Objective;
import com.model.Simplex;
import com.model.constants;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    public enum taskType{
        SIMPLEX,
        GRAPHICAL
    }

    private MutableLiveData<Restriction[]> restrictsMutable;
    public LiveData<Restriction[]> restricts = restrictsMutable;

    private MutableLiveData<Objective> mainFunctionMutable;
    public LiveData<Objective> mainFunction = mainFunctionMutable;

    private Model model;

    public SharedViewModel(){
        this.model = new Model();
    }

    public Object convertToGraphRestriction(Restriction[] restrictions){

        List<GraphRestriction> result = new ArrayList<>();

        for(Restriction restric : restrictions){
            double[] coeffs = restric.getDoubleCoeffs();

            if(coeffs.length != 2)
                return false;

            Float xValue = (float)coeffs[0];
            Float yValue = (float)coeffs[1];
            Float resultValue = (float)restric.getResultCoeffAsDouble() - (float)restric.getFreeCoeffAsDouble();
            constants.Sign sign = restric.sign;

            result.add(new GraphRestriction(xValue, yValue, sign, resultValue));
        }

        return result;
    }

    public Object convertToGraphObjective(Objective objective){

        double[] coeffs = objective.getDoubleCoeffs();

        if(coeffs.length != 2)
            return false;

        Float xValue = (float)coeffs[0];
        Float yValue = (float)coeffs[1];
        constants.GoalType goal = objective.goalType;

        return new GraphObjective(xValue, yValue, goal);
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
        mainFunc.goalType = goal;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getSolution(taskType task) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        Objective mainFunctionNormal = mainFunctionMutable.getValue();
        switch (task) {
            case SIMPLEX:
                Simplex.OutputData simplexOutputData = new Simplex.OutputData();
                simplexOutputData = model.getSimplexSolution(restrictionsNormal, mainFunctionNormal);
            case GRAPHICAL:
                Object restrictionConversionResult = new ArrayList<>(restrictionsNormal.length);
                Object objectiveConversionResult;
                restrictionConversionResult = convertToGraphRestriction(restrictionsNormal);
                objectiveConversionResult = convertToGraphObjective(mainFunctionNormal);
                if (!(restrictionConversionResult instanceof Boolean) && !(objectiveConversionResult instanceof Boolean)) {
                    List<GraphRestriction> restrictionsGraph = (List<GraphRestriction>)restrictionConversionResult;
                    GraphObjective mainFunctionGraph = (GraphObjective)objectiveConversionResult;
                    GraphSolver.OutputData graphOutputData = new GraphSolver.OutputData();
                    graphOutputData = model.getGraphSolution(restrictionsGraph, mainFunctionGraph);
                }

        }

    }

}
