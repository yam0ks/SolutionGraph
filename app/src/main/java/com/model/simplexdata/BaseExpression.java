package com.model.simplexdata;

import com.model.Fraction;

public class BaseExpression{
    protected Fraction[] coeffs;

    protected BaseExpression(double[] doubleCoeffs){
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
        return new Fraction(num);
    }

    public Fraction[] getCoeffs() {
        return coeffs;
    }

    public void setCoeffs(Fraction[] coeffs) {
        this.coeffs = coeffs;
    }

    public void setCoeff(int index, Fraction newValue) {
        this.coeffs[index] = newValue;
    }

    public void setCoeff(int index, double newValue) {
        this.coeffs[index] = new Fraction(newValue);
    }

    public double[] getDoubleCoeffs() {
        double[] arr = new double[coeffs.length];
        for (int i = 0; i < coeffs.length; i++) {
            arr[i] = coeffs[i].getDouble();
        }
        return arr;
    }
}
