package com.model.graphdata;

import com.github.mikephil.charting.data.Entry;
import com.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class GraphOutputData{ //Класс для представления результата работы графического метода
    public enum ErrorType{ //Перечисление с информацией о типе ошибке или ее отсутствии
        NOERROR,
        UNLIMITED,
        NOSOLUTION,
        UNKOWN,
        INCORRECTDATA
    }

    private List<GraphFunction> expressions; //Список полученных функций
    private Float rightBound;
    private Float leftBound;
    private Float topBound;
    private Float bottomBound;
    private Float xSolution; //X координата точки решения
    private Float ySolution; //Y координата точки решения
    private Float valueSolution; //Результат оптимизации
    private ErrorType error; //Тип ошибки

    public GraphOutputData(ErrorType type){
        expressions = new ArrayList<>();
        rightBound = 0F;
        leftBound = 0F;
        topBound = 0F;
        bottomBound = 0F;
        xSolution = 0F;
        ySolution = 0F;
        valueSolution = 0F;
        error = type;
    }

    public GraphOutputData(){
        expressions = new ArrayList<>();
        rightBound = 0F;
        leftBound = 0F;
        topBound = 0F;
        bottomBound = 0F;
        xSolution = 0F;
        ySolution = 0F;
        valueSolution = 0F;
        error = ErrorType.NOERROR;
    }

    public void setBounds(Float inputRightBound, Float inputLeftBound, Float inputTopBound,
                          Float inputBottomBound) {
        rightBound = inputRightBound;
        leftBound = inputLeftBound;
        topBound = inputTopBound;
        bottomBound = inputBottomBound;
    }

    public void setSolution(double xValue, double yValue, double resultValue) {
        xSolution = (float)xValue;
        ySolution = (float)yValue;
        valueSolution = (float)resultValue;
    }

    public void setExpressions(List<GraphFunction> inputExpressions){
        expressions = inputExpressions;
    }

    public List<GraphFunction> getExpressions() {
        return expressions;
    }

    public void setError(ErrorType type) {
        error = type;
    }

    public ErrorType getError() {
        return error;
    }

    public Float getLeftBound() {
        return leftBound;
    }

    public Float getRightBound() {
        return rightBound;
    }

    public Float getBottomBound() {
        return bottomBound;
    }

    public Float getTopBound() {
        return topBound;
    }

    public Float getValueSolution() {
        return valueSolution;
    }

    public Float getxSolution() {
        return xSolution;
    }

    public Float getySolution() {
        return ySolution;
    }
}