package com.model;

import com.github.mikephil.charting.utils.Utils;

public class GraphObjective extends BaseGraphExpression{ //Класс для представления целевой функции

    public constants.GoalType goal_type; //Направление целевой функции

    public GraphObjective(Float input_x_coeff, Float input_y_coeff, constants.GoalType type){
        super(input_x_coeff, input_y_coeff, 0F);
        goal_type = type;
        string_expression = AsString();
    }

    public void SetR_coeff(Float input_result_coeff){
        result_coeff = input_result_coeff;
    }

    @Override
    public void Normalize(){
        if(y_coeff < 0){
            x_coeff *= -1;
            y_coeff *= -1;
            result_coeff *= -1;
        }
        if(y_coeff == 0 && x_coeff < 0){
            x_coeff *= -1;
            result_coeff *= -1;
        }
    }

    @Override
    protected String AsString(){
        String result = "";

        if(x_coeff != 0) {
            if(x_coeff == 1)
                result += "x ";
            else if(x_coeff == -1)
                result += "-x ";
            else
                result += Utils.formatNumber(x_coeff, (x_coeff % 1 == 0) ? 0 : 1, false) + "x ";

            if(y_coeff > 0) {
                if (y_coeff == 1)
                    result += "+ y ";
                else
                    result += "+ " + Utils.formatNumber(Math.abs(y_coeff), (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
            }
            else if(y_coeff < 0)
                if(y_coeff == -1)
                    result += "- y ";
                else
                    result += "- " + Utils.formatNumber(Math.abs(y_coeff), (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
        }
        else{
            if(y_coeff == 1)
                result += "y ";
            else if(y_coeff == -1)
                result += "-y ";
            else
                result += Utils.formatNumber(y_coeff, (y_coeff % 1 == 0) ? 0 : 1, false) + "y ";
        }

        if(goal_type == constants.GoalType.MAXIMIZE)
            result += "-> max";
        else
            result += "-> min";

        return result;
    }
}