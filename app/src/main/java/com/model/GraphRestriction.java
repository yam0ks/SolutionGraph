package com.model;

import com.github.mikephil.charting.utils.Utils;

public class GraphRestriction extends BaseGraphExpression { //Класс для представления ограничения
    public constants.Sign sign; //Знак ограничения

    public GraphRestriction(Float inputXCoeff, Float inputYCoeff, constants.Sign sign,
                            Float inputResultCoeff) {
        super(inputXCoeff, inputYCoeff, inputResultCoeff);
        this.sign = sign;
        stringExpression = asString();
        Normalize();
    }

    @Override
    protected void Normalize() {
        if (yCoeff < 0) {
            xCoeff *= -1;
            yCoeff *= -1;
            resultCoeff *= -1;
            sign = (sign == constants.Sign.MORE) ? constants.Sign.LESS : constants.Sign.MORE;
        }
        if (yCoeff == 0 && xCoeff < 0) {
            xCoeff *= -1;
            resultCoeff *= -1;
        }
    }

    @Override
    public String asString() {
        String result = "";

        if (xCoeff != 0) {
            if (xCoeff == 1)
                result += "x ";
            else if (xCoeff == -1)
                result += "-x ";
            else
                result += Utils.formatNumber(xCoeff, (xCoeff % 1 == 0) ? 0 : 1,
                                                 false) + "x ";

            if (yCoeff > 0) {
                if (yCoeff == 1)
                    result += "+ y ";
                else
                    result += "+ " + Utils.formatNumber(Math.abs(yCoeff), (yCoeff % 1 == 0) ? 0 : 1,
                                                                      false) + "y ";
            } else if (yCoeff < 0)
                if (yCoeff == -1)
                    result += "- y ";
                else
                    result += "- " + Utils.formatNumber(Math.abs(yCoeff), (yCoeff % 1 == 0) ? 0 : 1,
                                                                      false) + "y ";
        } else {
            if (yCoeff == 1)
                result += "y ";
            else if (yCoeff == -1)
                result += "-y ";
            else
                result += Utils.formatNumber(yCoeff, (yCoeff % 1 == 0) ? 0 : 1,
                                                 false) + "y ";
        }

        if (sign == constants.Sign.MORE)
            result += "≥ " + Utils.formatNumber(resultCoeff, (resultCoeff % 1 == 0) ? 0 : 1,
                                                                     false);
        else
            result += "≤ " + Utils.formatNumber(resultCoeff, (resultCoeff % 1 == 0) ? 0 : 1,
                                                                     false);

        return result;
    }
}
