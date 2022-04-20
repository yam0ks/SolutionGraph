package com.model.simplexdata;

import com.model.Fraction;
import com.model.simplexdata.BaseExpression;
import com.utils.Constants;

public class Restriction extends BaseExpression {

    private Constants.Sign sign; //знак
    private Fraction result; //число после знака

    public Restriction(double[] coeffs, double freeCoeff, Constants.Sign sign, double res) {
        super(coeffs, freeCoeff);
        this.sign = sign;
        result = convertToFraction(res);
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

    public Constants.Sign getSign(){
        return sign;
    }

    public void setSign(Constants.Sign sign) {
        this.sign = sign;
    }
}
