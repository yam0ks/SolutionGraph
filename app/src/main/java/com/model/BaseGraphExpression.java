package com.model;

public class BaseGraphExpression { //Базовый класс для ограничений и целевой функции
    protected Float x_coeff; //Коэффициент при х
    protected Float y_coeff; //Коэффициент при у
    protected Float result_coeff; //Свободный коэффициент в правой части выражения
    protected String string_expression; //Строковое представление выражения

    protected BaseGraphExpression(Float input_x_coeff, Float input_y_coeff, Float input_result_coeff) {
        x_coeff = input_x_coeff;
        y_coeff = input_y_coeff;
        result_coeff = input_result_coeff;
    }

    public Float CalculateX(Float y_value){ //Расчет значения X при известном Y
        if(x_coeff == 0)
            return x_coeff;
        else if(y_coeff == 0)
            return  result_coeff;
        else
            return (result_coeff - y_coeff * y_value) / x_coeff;
    }

    public Float CalculateY(Float x_value){ //Расчет значения Y при известном X
        if(y_coeff == 0)
            return y_coeff;
        else if(x_coeff == 0)
            return result_coeff;
        else
            return (result_coeff - x_coeff * x_value) / y_coeff;

    }

    protected String AsString(){return "";} //Виртуальная функция для перевода выражения в строковое представление

    protected void Normalize(){} //Виртуальная функция для канонизации выражения
}