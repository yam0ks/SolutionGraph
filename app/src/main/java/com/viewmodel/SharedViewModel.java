package com.viewmodel;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;

import com.model.Fraction;
import com.model.graphdata.GraphOutputData;
import com.model.simplexdata.SimplexOutputData;
import com.usecase.GraphSolver;
import com.model.Model;
import com.model.simplexdata.Restriction;
import java.util.List;
import com.model.graphdata.GraphObjective;
import com.model.graphdata.GraphRestriction;
import com.model.simplexdata.Objective;
import com.usecase.Simplex;
import com.utils.Constants;
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
            Constants.Sign sign = restric.getSign();

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
        Constants.GoalType goal = objective.getGoalType();

        return new GraphObjective(xValue, yValue, goal);
    }

    public void changeRestrictCoeffs(int restrictIndex, int coeffIndex, double newValue) {
        Restriction[] restrictsNormal = restrictsMutable.getValue();
        Fraction[] fracsNormal = restrictsNormal[restrictIndex].getCoeffs();
        fracsNormal[coeffIndex] = new Fraction(newValue);
        restrictsNormal[restrictIndex].setCoeffs(fracsNormal);
        restrictsMutable.setValue(restrictsNormal);
    }

    public void changeRestrictFreeCoeff(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].setFreeCoeff(new Fraction(newValue));
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictRes(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].setResult(new Fraction(newValue));
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictSign(int restrictIndex, Constants.Sign newSign) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        restrictionsNormal[restrictIndex].setSign(newSign);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeMainFuncCoeff(int coeffIndex, double newValue) {
        Objective mainFunc = mainFunctionMutable.getValue();
        Fraction[] fracsNormal = mainFunc.getCoeffs();
        fracsNormal[coeffIndex] = new Fraction(newValue);
        mainFunc.setCoeffs(fracsNormal);
        mainFunctionMutable.setValue(mainFunc);
    }

    public void changeMainFuncGoalType(Constants.GoalType goal) {
        Objective mainFunc = mainFunctionMutable.getValue();
        mainFunc.setGoalType(goal);
        mainFunctionMutable.setValue(mainFunc);
    }

    public void createRestrictionData(int restrictionCount, int variableCount) {
        Restriction[] restrictionList = new Restriction[restrictionCount];
        double[] sampleCoeffs = new double[variableCount];
        for (int i = 0; i < variableCount; i++){
            sampleCoeffs[i] = 1;
        }
        for (int i = 0; i < restrictionCount; i++) {
            restrictionList[i] = new Restriction(sampleCoeffs, 0, Constants.Sign.EQUALS, 0);
        }
        restrictsMutable.setValue(restrictionList);
    }

    public void createMainFuncData(int variableCount) {
        double[] sampleCoeffs = new double[variableCount];
        for (int i = 0; i < variableCount; i++) {
            sampleCoeffs[i] = 1;
        }
        Objective mainFunc = new Objective(sampleCoeffs, Constants.GoalType.MAXIMIZE);
        mainFunctionMutable.setValue(mainFunc);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getSolution(taskType task) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        Objective mainFunctionNormal = mainFunctionMutable.getValue();
        switch (task) {
            case SIMPLEX:
                SimplexOutputData simplexOutputData = new SimplexOutputData();
                simplexOutputData = model.getSimplexSolution(restrictionsNormal, mainFunctionNormal);
            case GRAPHICAL:
                Object restrictionConversionResult;
                Object objectiveConversionResult;
                restrictionConversionResult = convertToGraphRestriction(restrictionsNormal);
                objectiveConversionResult = convertToGraphObjective(mainFunctionNormal);
                if (!(restrictionConversionResult instanceof Boolean) && !(objectiveConversionResult instanceof Boolean)) {
                    List<GraphRestriction> restrictionsGraph = (List<GraphRestriction>)restrictionConversionResult;
                    GraphObjective mainFunctionGraph = (GraphObjective)objectiveConversionResult;
                    GraphOutputData graphOutputData = model.getGraphSolution(restrictionsGraph, mainFunctionGraph);
                }

        }

    }

}
