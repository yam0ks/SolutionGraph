package com.model;

public class Restriction extends BaseExpression{
    public enum Sign {
        MORE,
        LESS,
        EQUAL
    }

    public Fraction freeCoeff; //свободный коэффициент
    public Sign sign; //знак
    public Fraction result; //число после знака

    public Restriction(double[] coeffs, double f_coeff, Sign s, double res) {
        super(coeffs);
        freeCoeff = ConvertToFraction(f_coeff);
        sign = s;
        result = ConvertToFraction(res);
    }
}
