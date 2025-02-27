package com.model.simplexdata;

import com.model.simplexdata.BaseExpression;
import com.utils.Constants;

public class Objective extends BaseExpression {
    private Constants.GoalType goalType;

    public Objective(Double[] coeffs, Double freeCoeff, Constants.GoalType goal){
        super(coeffs, freeCoeff);
        goalType = goal;
    }

    public double getFreeCoeff() {
        return super.getDoubleFreeCoeff();
    }

    public Constants.GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(Constants.GoalType goalType) {
        this.goalType = goalType;
    }
}
