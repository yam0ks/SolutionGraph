package com.model.simplexdata;

import com.model.Fraction;

public class BaseExpression{
    protected Fraction[] coeffs;
    private String[] coeffsString;
    private Fraction freeCoeff;
    private String freeCoeffString;

    protected BaseExpression(Fraction[] coeffs, Fraction freeCoeff) {
        this.coeffs = coeffs;
        this.coeffsString = convertFractionsToStrings(coeffs);
        this.freeCoeff = freeCoeff;
        this.freeCoeffString = freeCoeff.toString();
    }

    protected BaseExpression(Double[] doubleCoeffs, Double doubleFreeCoeff) {
        this.coeffs = convertDoublesToFractions(doubleCoeffs);
        this.coeffsString = convertDoublesToStrings(doubleCoeffs);
        this.freeCoeff = new Fraction(doubleFreeCoeff);
        this.freeCoeffString = freeCoeff.toString();
    }

    private Fraction[] convertDoublesToFractions(Double[] convertValue) {
        Fraction[] result = new Fraction[convertValue.length];
        for (int i = 0; i < convertValue.length; ++i){
            result[i] = new Fraction(convertValue[i]);
        }
        return result;
    }

    private String[] convertDoublesToStrings(Double[] convertValue) {
        String[] result = new String[convertValue.length];
        for (int i = 0; i < convertValue.length; ++i){
            result[i] = convertValue.toString();
        }
        return result;
    }

    private String[] convertFractionsToStrings(Fraction[] convertValue) {
        String[] result = new String[convertValue.length];
        for (int i = 0; i < convertValue.length; ++i){
            result[i] = convertValue.toString();
        }
        return result;
    }

    public Fraction[] getFractionCoeffs() {
        return coeffs;
    }

    public Double[] getDoubleCoeffs() {
        Double[] arr = new Double[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            arr[i] = coeffs[i].getDouble();
        }
        return arr;
    }

    public void setCoeffsFraction(Fraction[] coeffs) {
        this.coeffs = coeffs;
        this.coeffsString = convertFractionsToStrings(coeffs);
    }

    public void setCoeffsDouble(Double[] coeffs) {
        this.coeffs = convertDoublesToFractions(coeffs);
        this.coeffsString = convertDoublesToStrings(coeffs);
    }

    public void setCoeffFraction(int index, Fraction newValue) {
        if (index >= coeffs.length || index < 0) return;
        this.coeffs[index] = newValue;
        this.coeffsString[index] = newValue.toString();
    }

    public void setCoeffDouble(int index, Double newValue) {
        if (index >= coeffs.length || index < 0) return;
        this.coeffs[index] = new Fraction(newValue);
        this.coeffsString[index] = newValue.toString();
    }

    public Double getDoubleFreeCoeff() {
        return freeCoeff.getDouble();
    }

    public Fraction getFractionFreeCoeff() {
        return freeCoeff;
    }

    public void setFreeCoeffFraction(Fraction freeCoeff) {
        this.freeCoeff = freeCoeff;
        this.freeCoeffString = freeCoeff.toString();
    }

    public void setFreeCoeffDouble(Double freeCoeff) {
        this.freeCoeff = new Fraction(freeCoeff);
        this.freeCoeffString = freeCoeff.toString();
    }
}
