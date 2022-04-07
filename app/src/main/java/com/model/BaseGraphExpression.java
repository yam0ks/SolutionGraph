package com.model;

public class BaseGraphExpression { //Базовый класс для ограничений и целевой функции
    protected Float xCoeff; //Коэффициент при х
    protected Float yCoeff; //Коэффициент при у
    protected Float resultCoeff; //Свободный коэффициент в правой части выражения
    protected String stringExpression; //Строковое представление выражения

    protected BaseGraphExpression(Float inputXCoeff, Float inputYCoeff, Float inputResultCoeff) {
        xCoeff = inputXCoeff;
        yCoeff = inputYCoeff;
        resultCoeff = inputResultCoeff;
    }

    public Float calculateX(Float yValue){ //Расчет значения X при известном Y
        if(xCoeff == 0)
            return xCoeff;
        else if(yCoeff == 0)
            return resultCoeff;
        else
            return (resultCoeff - yCoeff * yValue) / xCoeff;
    }

    public Float calculateY(Float xValue){ //Расчет значения Y при известном X
        if(yCoeff == 0)
            return yCoeff;
        else if(xCoeff == 0)
            return resultCoeff;
        else
            return (resultCoeff - xCoeff * xValue) / yCoeff;

    }

    protected String asString(){return "";} //Виртуальная функция для перевода выражения в строковое представление

    protected void Normalize(){} //Виртуальная функция для канонизации выражения
}