package com.model.simplexdata;

import com.model.Fraction;

import java.util.ArrayList;

public class SimplexOutputData {
    private SimplexInitData initData; //Раздел Начальная симплекс матрийа
    private ArrayList<SimplexNormalizeData> normalizeData; //Раздел ищем начальное базисное решение
    private ArrayList<SimplexSolutionData> solutionData; //Раздел вычисляем дельты
    private Fraction[] answers; // Ответ от 0 до length-2 - коэффициенты при соответветсвующих
    // иксах последний элемент - ответ

    public SimplexOutputData(){}

    public void setAnswers(Fraction[] answers){ this.answers = answers; }

    public SimplexInitData getInitData() {
        return initData;
    }

    public void setInitData(SimplexInitData initData){
        this.initData = initData;
    }

    public ArrayList<SimplexNormalizeData> getNormalizeData() {
        return normalizeData;
    }

    public void setNormalizeData(ArrayList<SimplexNormalizeData> normalizeData){
        this.normalizeData = normalizeData;
    }

    public ArrayList<SimplexSolutionData> getSolutionData() {
        return solutionData;
    }

    public void setSolutionData(ArrayList<SimplexSolutionData> solutionData){
        this.solutionData = solutionData;
    }

    public Fraction[] getAnswers() {
        return answers;
    }
}