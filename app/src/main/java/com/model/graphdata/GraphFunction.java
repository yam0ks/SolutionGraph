package com.model.graphdata;

import com.github.mikephil.charting.data.Entry;
import com.utils.Constants;

import java.util.List;

public class GraphFunction{ //Класс для представления функций
    public enum Type { //Перечисление с информацией о типе графика (обычный, целевая функция,
        // искусственная пристройка)
        DEFAULT,
        ARTIFICIAL,
        PARALLEL,
        OBJECTIVE
    }

    private List<Entry> points; //Список с точками графика
    private Constants.Sign sign; //Знак ограничения
    private String strExpression; //Строковое представление выражения
    private Type type; //Тип графика

    public GraphFunction(List<Entry> inputPoints, Constants.Sign sign, String inputStrExpression,
                      Type inputType) {
        points = inputPoints;
        this.sign = sign;
        strExpression = inputStrExpression;
        type = inputType;
    }

    public GraphFunction(List<Entry> inputPoints, String inputStrExpression, Type inputType) {
        points = inputPoints;
        strExpression = inputStrExpression;
        type = inputType;
        sign = Constants.Sign.EQUALS;
    }

    public List<Entry> getPoints() {
        return points;
    }

    public Constants.Sign getSign(){
        return sign;
    }

    public String getStrExpression(){
        return strExpression;
    }

    public Type getType(){
        return type;
    }
}