package com.model;

public class Restriction extends BaseExpression{

    public Fraction freeCoeff; //свободный коэффициент
    public constants.Sign sign; //знак
    public Fraction result; //число после знака

    public Restriction(double[] coeffs, double freeCoeff, constants.Sign sign, double res) {
        super(coeffs);
        this.freeCoeff = convertToFraction(freeCoeff);
        this.sign = sign;
        result = convertToFraction(res);
    }

    public double getFreeCoeffAsDouble(){
        return freeCoeff.getDouble();
    }

    public double getResultCoeffAsDouble(){
        return result.getDouble();
    }
}
