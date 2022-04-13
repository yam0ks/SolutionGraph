package com.model.simplexdata;

import com.model.Fraction;
import com.model.simplexdata.BaseExpression;
import com.utils.Constants;

public class Restriction extends BaseExpression {

    private Fraction freeCoeff; //свободный коэффициент
    private Constants.Sign sign; //знак
    private Fraction result; //число после знака

    public Restriction(double[] coeffs, double freeCoeff, Constants.Sign sign, double res) {
        super(coeffs);
        this.freeCoeff = convertToFraction(freeCoeff);
        this.sign = sign;
        result = convertToFraction(res);
    }

    public double getFreeCoeffAsDouble(){
        return freeCoeff.getDouble();
    }

    public double getResultAsDouble(){
        return result.getDouble();
    }

    public Fraction getResult() {
        return result;
    }

    public void setResult(Fraction result) {
        this.result = result;
    }

    public Fraction getFreeCoeff() {
        return freeCoeff;
    }

    public void setFreeCoeff(Fraction freeCoeff) {
        this.freeCoeff = freeCoeff;
    }

    public Constants.Sign getSign(){
        return sign;
    }

    public void setSign(Constants.Sign sign) {
        this.sign = sign;
    }
}
