package com.model.simplexdata;

import com.model.Fraction;
import com.utils.Constants;

public class Restriction extends BaseExpression {
    private Constants.Sign sign; //знак
    private Fraction result; //число после знака
    private String resultString;

    public Restriction(Double[] coeffs, Double freeCoeff, Constants.Sign sign, Double result) {
        super(coeffs, freeCoeff);
        this.sign = sign;
        this.result = new Fraction(result);
        this.resultString = result.toString();
    }

    public Double getResultAsDouble(){
        return result.getDouble();
    }

    public Fraction getResult() {
        return result;
    }

    public void setResult(Fraction result) {
        this.result = result;
        this.resultString = result.toString();
    }

    public void setResultDouble(Double result) {
        this.result = new Fraction(result);
        this.resultString = result.toString();
    }

    public String getResultString() {
        return resultString;
    }

    public Constants.Sign getSign(){
        return sign;
    }

    public void setSign(Constants.Sign sign) {
        this.sign = sign;
    }
}
