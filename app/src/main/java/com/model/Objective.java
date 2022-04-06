package com.model;

public class Objective extends BaseExpression{
    public constants.GoalType goal_type;

    public Objective(double[] coeefs, constants.GoalType goal){
        super(coeefs);
        goal_type = goal;
    }
}
