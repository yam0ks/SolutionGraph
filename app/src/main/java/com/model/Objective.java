package com.model;

public class Objective extends BaseExpression{
    public constants.GoalType goalType;

    public Objective(double[] coeffs, constants.GoalType goal){
        super(coeffs);
        goalType = goal;
    }
}
