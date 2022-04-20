package com.viewmodel;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;

import com.model.Fraction;
import com.model.graphdata.GraphOutputData;
import com.model.simplexdata.Section;
import com.model.simplexdata.SimplexOutputData;
import com.model.Model;
import com.model.simplexdata.Restriction;
import java.util.List;
import com.model.graphdata.GraphObjective;
import com.model.graphdata.GraphRestriction;
import com.model.simplexdata.Objective;
import com.usecase.SimplexParser;
import com.utils.Constants;
import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    public enum taskType{
        SIMPLEX,
        GRAPHICAL
    }

    private final MutableLiveData<Restriction[]> restrictsMutable = new MutableLiveData<>();
    public LiveData<Restriction[]> restricts = restrictsMutable;

    private final MutableLiveData<Objective> objectiveMutable = new MutableLiveData<>();
    public LiveData<Objective> objective = objectiveMutable;

    private final MutableLiveData<Section[]> sectionsMutable = new MutableLiveData<>();
    public LiveData<Section[]> sections = sectionsMutable;

    private final MutableLiveData<Boolean> isLoadingMutable = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = isLoadingMutable;

    private final Model model;
    private final SimplexParser simplexParser;

    public SharedViewModel(){
        this.model = new Model();
        this.simplexParser = new SimplexParser();
    }

    public Object convertToGraphRestriction(Restriction[] restrictions){

        List<GraphRestriction> result = new ArrayList<>();

        for(Restriction restriction : restrictions){
            double[] coeffs = restriction.getDoubleCoeffs();

            if(coeffs.length != 2)
                return false;

            Float xValue = (float)coeffs[0];
            Float yValue = (float)coeffs[1];
            Float resultValue = (float)restriction.getResultAsDouble() - (float)restriction.getDoubleFreeCoeff();
            Constants.Sign sign = restriction.getSign();

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

    public void saveRestrictions(Restriction[] restrictions) {
        restrictsMutable.setValue(restrictions);
    }

    public void saveObjective(Objective mainFunc) {
        objectiveMutable.setValue(mainFunc);
    }

    public void changeRestrictCoeffs(int restrictIndex, int coeffIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        if (restrictionsNormal == null) return;
        if (restrictIndex >= restrictionsNormal.length || restrictIndex < 0) return;
        restrictionsNormal[restrictIndex].setCoeffDouble(coeffIndex, newValue);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictFreeCoeff(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        if (restrictionsNormal == null) return;
        if (restrictIndex >= restrictionsNormal.length || restrictIndex < 0) return;
        restrictionsNormal[restrictIndex].setFreeCoeffDouble(newValue);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictRes(int restrictIndex, double newValue) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        if (restrictionsNormal == null) return;
        if (restrictIndex >= restrictionsNormal.length || restrictIndex < 0) return;
        restrictionsNormal[restrictIndex].setResult(new Fraction(newValue));
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeRestrictSign(int restrictIndex, Constants.Sign newSign) {
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        if (restrictionsNormal == null) return;
        if (restrictIndex >= restrictionsNormal.length || restrictIndex < 0) return;
        restrictionsNormal[restrictIndex].setSign(newSign);
        restrictsMutable.setValue(restrictionsNormal);
    }

    public void changeObjectiveCoeff(int coeffIndex, double newValue) {
        Objective objective = objectiveMutable.getValue();
        if (objective == null) return;
        objective.setCoeffDouble(coeffIndex, newValue);
        objectiveMutable.setValue(objective);
    }

    public void changeObjectiveGoalType(Constants.GoalType goal) {
        Objective objective = objectiveMutable.getValue();
        if (objective == null) return;
        objective.setGoalType(goal);
        objectiveMutable.setValue(objective);
    }

    public void createRestrictionData(int restrictionCount, int variableCount) {
        if (restrictionCount < 0 || variableCount < 0) return;
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

    public void createObjectiveData(int variableCount) {
        double[] sampleCoeffs = new double[variableCount];
        for (int i = 0; i < variableCount; i++) {
            sampleCoeffs[i] = 1;
        }
        Objective objective = new Objective(sampleCoeffs, 0, Constants.GoalType.MAXIMIZE);
        objectiveMutable.setValue(objective);
    }

    private void beautifyData(SimplexOutputData simplexData) {
        simplexParser.setData(simplexData, true);
        sectionsMutable.setValue(simplexParser.getSections());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getSolution(taskType task) {
        isLoadingMutable.setValue(true);
        Restriction[] restrictionsNormal = restrictsMutable.getValue();
        Objective objectiveNormal = objectiveMutable.getValue();
        if (restrictionsNormal == null || objectiveNormal == null) {
            isLoadingMutable.setValue(false);
            return;
        }
        switch (task) {
            case SIMPLEX:
                SimplexOutputData simplexOutputData =
                        model.getSimplexSolution(restrictionsNormal, objectiveNormal);
                beautifyData(simplexOutputData);
            case GRAPHICAL:
                Object restrictionConversionResult = convertToGraphRestriction(restrictionsNormal);
                Object objectiveConversionResult = convertToGraphObjective(objectiveNormal);
                if (!(restrictionConversionResult instanceof Boolean) && !(objectiveConversionResult instanceof Boolean)) {
                    List<GraphRestriction> restrictionsGraph = (List<GraphRestriction>)restrictionConversionResult;
                    GraphObjective mainFunctionGraph = (GraphObjective)objectiveConversionResult;
                    GraphOutputData graphOutputData = model.getGraphSolution(restrictionsGraph, mainFunctionGraph);
                }

        }
        isLoadingMutable.setValue(false);
    }
}
