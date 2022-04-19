package com.model.simplexdata;

import com.model.Fraction;

public class BaseExpression{
    protected Fraction[] coeffs;
    private Fraction freeCoeff; //свободный коэффициент

    protected BaseExpression(Fraction[] coeffs, Fraction freeCoeff){
        this.coeffs = coeffs;
        this.freeCoeff = freeCoeff;
    }

    protected BaseExpression(double[] doubleCoeffs, double doubleFreeCoeff){
        coeffs = convertToFraction(doubleCoeffs);
        freeCoeff = convertToFraction(doubleFreeCoeff);
    }

    protected Fraction[] convertToFraction(double[] convertValue){
        Fraction[] result = new Fraction[convertValue.length];
        for (int i = 0; i < convertValue.length; ++i){
            result[i] = new Fraction(convertValue[i]);
        }
        return result;
    }

    protected Fraction convertToFraction(double convertValue){
        return new Fraction(convertValue);
    }


    public Fraction[] getFractionCoeffs() {
        return coeffs;
    }

    public double[] getDoubleCoeffs() {
        double[] arr = new double[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            arr[i] = coeffs[i].getDouble();
        }
        return arr;
    }

    public void setCoeffsFraction(Fraction[] coeffs) {
        this.coeffs = coeffs;
    }

    public void setCoeffsDouble(double[] coeffs) {
        this.coeffs = convertToFraction(coeffs);
    }


    public void setCoeffFraction(int index, Fraction newValue) {
        this.coeffs[index] = newValue;
    }

    public void setCoeffDouble(int index, double newValue) {
        if (index >= coeffs.length || index < 0) return;
        this.coeffs[index] = new Fraction(newValue);
    }


    public double getDoubleFreeCoeff(){
        return freeCoeff.getDouble();
    }

    public Fraction getFractionFreeCoeff() {
        return freeCoeff;
    }

    public void setFreeCoeffFraction(Fraction freeCoeff) {
        this.freeCoeff = freeCoeff;
    }

    public void setFreeCoeffDouble(double freeCoeff) {
        this.freeCoeff = convertToFraction(freeCoeff);
    }
}
