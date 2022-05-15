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

    public boolean checkConditionFulfillment(float x, float y){
        float lhs = xCoeff * x + yCoeff * y;

        if(lhs - resultCoeff < 0.001)
            return true;

        switch(sign){
            case LESS:
                return Float.compare(lhs, resultCoeff) <= 0;
            case MORE:
                return Float.compare(lhs, resultCoeff) >= 0;
            case EQUALS:
                return Float.compare(lhs, resultCoeff) == 0;
            default: break;
        }

        return false;
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
            result += "≥ ";
        else if(sign == Constants.Sign.LESS)
            result += "≤ ";
        else
            result += "= ";

        result  += Utils.formatNumber(resultCoeff, (resultCoeff % 1 == 0) ? 0 : 1,
                false);

        return result;
    }

    @Override
    protected void normalize() {
        if (yCoeff < 0) {
            xCoeff *= (xCoeff == 0F) ? 1 : -1;
            yCoeff *= -1;
            resultCoeff *= (resultCoeff == 0F) ? 1 : -1;

            if(sign == Constants.Sign.MORE)
                sign = Constants.Sign.LESS;
            else if(sign == Constants.Sign.LESS)
                sign = Constants.Sign.MORE;
        }
        if (yCoeff == 0 && xCoeff < 0) {
            xCoeff *= -1;
            resultCoeff *= (resultCoeff == 0F) ? 1 : -1;
        }
    }
}
