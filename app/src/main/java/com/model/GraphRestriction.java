package com.model;

import com.github.mikephil.charting.utils.Utils;

public class GraphRestriction extends BaseGraphExpression { //Класс для представления ограничения
    public enum Sign { //Перечисление с информацией о знаке ограничения
        EQ,
        GEQ,
        LEQ
    }

    public Sign sign; //Знак ограничения

    public GraphRestriction(Float x_c, Float y_c, Sign s, Float r_c) {
        super(x_c, y_c, r_c);
        sign = s;
        result_coeff = r_c;
        string_expression = AsString();
        Normalize();
    }

    @Override
    protected void Normalize() {
        if (y_coeff < 0) {
            x_coeff *= -1;
            y_coeff *= -1;
            result_coeff *= -1;
            sign = (sign == Sign.GEQ) ? Sign.LEQ : Sign.GEQ;
        }
        if (y_coeff == 0 && x_coeff < 0) {
            x_coeff *= -1;
            result_coeff *= -1;
        }
    }

    @Override
    public String AsString() {
        String result = "";

        if (x_coeff != 0) {
            if (x_coeff == 1)
                result += "x ";
            else if (x_coeff == -1)
                result += "-x ";
            else
                result += Utils.formatNumber(x_coeff, (x_coeff % 1 == 0) ? 0 : 1, false) + "x ";

            if (y_coeff > 0) {
                if (y_coeff == 1)
                    result += "+ y ";
                else
                    result += "+ " + Utils.formatNumber(Math.abs(y_coeff), (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
            } else if (y_coeff < 0)
                if (y_coeff == -1)
                    result += "- y ";
                else
                    result += "- " + Utils.formatNumber(Math.abs(y_coeff), (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
        } else {
            if (y_coeff == 1)
                result += "y ";
            else if (y_coeff == -1)
                result += "-y ";
            else
                result += Utils.formatNumber(y_coeff, (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
        }

        if (sign == Sign.GEQ)
            result += "≥ " + Utils.formatNumber(result_coeff, (result_coeff % 1 == 0) ? 0 : 1, false);
        else
            result += "≤ " + Utils.formatNumber(result_coeff, (result_coeff % 1 == 0) ? 0 : 1, false);

        return result;
    }
}
