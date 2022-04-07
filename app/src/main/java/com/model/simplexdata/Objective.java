package com.model.simplexdata;

import com.model.simplexdata.BaseExpression;
import com.utils.Constants;

public class Objective extends BaseExpression {
    private Constants.GoalType goalType;

    public Objective(double[] coeffs, Constants.GoalType goal){
        super(coeffs);
        goalType = goal;
    }

    public Constants.GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(Constants.GoalType goalType) {
        this.goalType = goalType;
    }
}
