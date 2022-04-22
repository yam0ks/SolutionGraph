package com.model.graphdata;

import com.github.mikephil.charting.utils.Utils;
import com.utils.Constants;

public class GraphObjective extends BaseGraphExpression { //Класс для представления целевой функции

    private Constants.GoalType goalType; //Направление целевой функции

    public GraphObjective(Float inputXCoeff, Float inputYCoeff, Float inputResultCoeff, Constants.GoalType type){
        super(inputXCoeff, inputYCoeff, inputResultCoeff);
        goalType = type;
        stringExpression = getExpressionAsString();
    }

    public void setResultCoeff(Float inputResultCoeff){
        resultCoeff = inputResultCoeff;
    }

    @Override
    public void normalize(){
        if(yCoeff < 0){
            xCoeff *= -1;
            yCoeff *= -1;
            resultCoeff *= -1;
        }
        if(yCoeff == 0 && xCoeff < 0){
            xCoeff *= -1;
            resultCoeff *= -1;
        }
    }

    public Constants.GoalType getGoalType(){
        return goalType;
    }

    public Float getResultCoeff() {
        return super.getResultCoeff();
    }

    @Override
    protected String getExpressionAsString(){
        String result = "";

        if(xCoeff != 0) {
            if(xCoeff == 1)
                result += "x ";
            else if(xCoeff == -1)
                result += "-x ";
            else
                result += Utils.formatNumber(xCoeff, (xCoeff % 1 == 0) ? 0 : 1,
                                                 false) + "x ";

            if(yCoeff > 0) {
                if (yCoeff == 1)
                    result += "+ y ";
                else
                    result += "+ " + Utils.formatNumber(Math.abs(yCoeff), (yCoeff % 1 == 0) ? 0 : 1,
                                                                      false) + "y ";
            }
            else if(yCoeff < 0)
                if(yCoeff == -1)
                    result += "- y ";
                else
                    result += "- " + Utils.formatNumber(Math.abs(yCoeff), (yCoeff % 1 == 0) ? 0 : 1,
                                                                      false) + "y ";
        }
        else{
            if(yCoeff == 1)
                result += "y ";
            else if(yCoeff == -1)
                result += "-y ";
            else
                result += Utils.formatNumber(yCoeff, (yCoeff % 1 == 0) ? 0 : 1, false) + "y ";
        }

        if(goalType == Constants.GoalType.MAXIMIZE)
            result += "-> max";
        else
            result += "-> min";

        return result;
    }
}