package com.model;

import com.github.mikephil.charting.utils.Utils;

public class GraphRestriction extends BaseGraphExpression { //Класс для представления ограничения
    public constants.Sign sign; //Знак ограничения

    public GraphRestriction(Float input_x_coeff, Float input_y_coeff, constants.Sign s, Float input_result_coeff) {
        super(input_x_coeff, input_y_coeff, input_result_coeff);
        sign = s;
        string_expression = AsString();
        Normalize();
    }

    @Override
    protected void Normalize() {
        if (y_coeff < 0) {
            x_coeff *= -1;
            y_coeff *= -1;
            result_coeff *= -1;
            sign = (sign == constants.Sign.GEQ) ? constants.Sign.LEQ : constants.Sign.GEQ;
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

        if (sign == constants.Sign.GEQ)
            result += "≥ " + Utils.formatNumber(result_coeff, (result_coeff % 1 == 0) ? 0 : 1, false);
        else
            result += "≤ " + Utils.formatNumber(result_coeff, (result_coeff % 1 == 0) ? 0 : 1, false);

        return result;
    }
}
