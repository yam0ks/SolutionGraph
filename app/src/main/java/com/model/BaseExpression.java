package com.model;

public class BaseExpression{
    protected Fraction[] coeffs;

    BaseExpression(double[] doubleCoeffs){
        coeffs = convertToFraction(doubleCoeffs);
    }

    protected Fraction[] convertToFraction(double[] nums){
        Fraction[] result = new Fraction[nums.length];

        for (int i = 0; i < nums.length; ++i){
            result[i] = new Fraction(nums[i]);
        }

        return result;
    }

    protected Fraction convertToFraction(double num){
        Fraction result = new Fraction(num);
        return result;
    }

    public Fraction[] getFractionCoeffs() {
        return coeffs;
    }

    public void setFractionCoeffs (Fraction[] coeffs) {
        this.coeffs = coeffs;
    }

    public double[] getDoubleCoeffs() {
        double[] arr = new double[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            arr[i] = coeffs[i].getDouble();
        }
        return arr;
    }
}
