package com.model;

public class Model {
    private Simplex simplex;
    private Simplex.InputData inputData;
    private Simplex.OutputData outputData;
    private int restrictionCount;
    private int variableCount;

    private enum MinMax{
        MIN,
        MAX
    }

    public Model(){
        simplex = new Simplex();
        //graph = new Graph();
    }

    private void initializeData(){
        inputData = new Simplex.InputData();
        outputData = new Simplex.OutputData();
    }

    private void setCounts(int restrictionCount, int variableCount){
        this.restrictionCount = restrictionCount;
        this.variableCount = variableCount;
    }

    private Simplex.OutputData getSimplexSolution(double[][] rawRestrictions, double[] rawMainFunc){
        initializeData();
        rawDataToFraction(rawRestrictions, rawMainFunc);
        simplex.SetInputData(inputData);
        outputData = simplex.startWork();
        return outputData;
    }

    private Simplex.OutputData getGraphSolution(double[][] rawRestrictions, double[] rawMainFunc){
        //Надо сначала сделать graph
        return outputData;
    }

    private void rawDataToFraction(double[][] rawRestrictions, double[] rawMainFunc){
        Fraction[][] restrictions = new Fraction[variableCount + 2][restrictionCount];
        Fraction[] mainFunc = new Fraction[variableCount + 1];
        Simplex.Sign[] signs = new Simplex.Sign[restrictionCount];
        boolean findMax;

        for (int i = 0; i < variableCount + 2; i++){
            for(int j = 1; j < restrictionCount; j++){
                restrictions[i][j - 1] = new Fraction(rawRestrictions[i][j]);
            }
        }

        for (int i = 1; i < variableCount + 1; i++)
        {
            mainFunc[i - 1] = new Fraction(rawMainFunc[i]);
        }

        for (int i = 0; i < restrictionCount; i++)
        {
            signs[i] = Simplex.Sign.values()[(int)rawMainFunc[i]];
        }

        findMax = rawMainFunc[0] == MinMax.MAX.ordinal();

        setInputDataParams(restrictions, mainFunc, signs, findMax);
    }

    private void setInputDataParams(Fraction[][] restrictions,  Fraction[] mainFunc,
                                    Simplex.Sign[] signs, boolean findMax){
        inputData.restrictionsCoeff = restrictions;
        inputData.mainFuncCoeff = mainFunc;
        inputData.comparisonSings = signs;
        inputData.findMax = findMax;
    }
}
