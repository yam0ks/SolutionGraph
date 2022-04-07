package com.model.graphdata;

import com.github.mikephil.charting.utils.Utils;
import com.utils.Constants;

public class GraphRestriction extends BaseGraphExpression { //Класс для представления ограничения
    private Constants.Sign sign; //Знак ограничения

    public GraphRestriction(Float inputXCoeff, Float inputYCoeff, Constants.Sign sign,
                            Float inputResultCoeff) {
        super(inputXCoeff, inputYCoeff, inputResultCoeff);
        this.sign = sign;
        stringExpression = getExpressionAsString();
        normalize();
    }

    public Constants.Sign getSign() {
        return sign;
    }

    @Override
    public String getExpressionAsString() {
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

        if (sign == Constants.Sign.MORE)
            result += "≥ " + Utils.formatNumber(resultCoeff, (resultCoeff % 1 == 0) ? 0 : 1,
                                                                     false);
        else
            result += "≤ " + Utils.formatNumber(resultCoeff, (resultCoeff % 1 == 0) ? 0 : 1,
                                                                     false);

        return result;
    }

    @Override
    protected void normalize() {
        if (yCoeff < 0) {
            xCoeff *= -1;
            yCoeff *= -1;
            resultCoeff *= -1;
            sign = (sign == Constants.Sign.MORE) ? Constants.Sign.LESS : Constants.Sign.MORE;
        }
        if (yCoeff == 0 && xCoeff < 0) {
            xCoeff *= -1;
            resultCoeff *= -1;
        }
    }
}
