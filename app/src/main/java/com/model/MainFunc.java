package com.model;

public class MainFunc extends BaseExpression{
    public boolean minMax;

    MainFunc(double[] coeefs, boolean MinMax){
        super(coeefs);
        minMax = MinMax;
    }
}
