package com.backend;

public class Fraction {
    private int numerator;
    private int denominator;

    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        normalizeFraction();
    }

    public Fraction(int integer) {
        this.numerator = integer;
        this.denominator = 1;
    }

    public Fraction(Fraction fraction) {
        this.numerator = fraction.getNumerator();
        this.denominator = fraction.getDenominator();
    }

    public Fraction(double number){
        String str = String.valueOf(number);
        str.indexOf('.');
        int denominator = (int)Math.pow(10, str.length() - str.indexOf('.') - 1);
        this.denominator = denominator;
        this.numerator = (int)(number*denominator);
        normalizeFraction();
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    private void normalizeFraction(){
        int i = 2;
        while (i <= Math.abs(this.denominator) && i <= Math.abs(this.numerator))
            for (i = 2; i <= Math.abs(this.denominator) && i <= Math.abs(this.numerator); i++) {
                if (this.numerator % i == 0 && this.denominator % i == 0) {
                    this.numerator = this.numerator / i;
                    this.denominator = this.denominator / i;
                    break;
                }
            }

        if (this.numerator == 0)
            this.denominator = 1;

        if (this.denominator == 0)
            this.numerator = 0;

        if (this.numerator < 0 && this.denominator < 0) {
            this.numerator *= -1;
            this.denominator *= -1;
        }
        if (this.numerator > 0 && this.denominator < 0) {
            this.numerator *= -1;
            this.denominator *= -1;
        }
    }

    private Fraction normalizeFraction(Fraction fraction) {
        int i = 2;
        while (i <= Math.abs(fraction.denominator) && i <= Math.abs(fraction.numerator))
            for (i = 2; i <= Math.abs(fraction.denominator) && i <= Math.abs(fraction.numerator); i++) {
                if (fraction.numerator % i == 0 && fraction.denominator % i == 0) {
                    fraction.numerator = fraction.numerator / i;
                    fraction.denominator = fraction.denominator / i;
                    break;
                }
            }

        if (fraction.numerator == 0)
            fraction.denominator = 1;

        if (fraction.denominator == 0)
            fraction.numerator = 0;

        if (fraction.numerator < 0 && fraction.denominator < 0) {
            fraction.numerator *= -1;
            fraction.denominator *= -1;
        }
        if (fraction.numerator > 0 && fraction.denominator < 0) {
            fraction.numerator *= -1;
            fraction.denominator *= -1;
        }
        return fraction;
    }

    public Fraction getSum(Fraction b) {
        int num = this.numerator * b.denominator + b.numerator * this.denominator;
        int den = this.denominator * b.denominator;
        return normalizeFraction(new Fraction(num, den));
    }

    public Fraction getSum(int b) {
        int num = this.numerator + (b * this.denominator);
        return normalizeFraction(new Fraction(num, this.denominator));
    }

    public Fraction getMinus(Fraction b) {
        int num = this.numerator * b.denominator - b.numerator * this.denominator;
        int den = this.denominator * b.denominator;
        return normalizeFraction(new Fraction(num, den));
    }

    public Fraction getMinus(int b) {
        int num = this.numerator - (b * this.denominator);
        return normalizeFraction(new Fraction(num, this.denominator));
    }

    public Fraction getMultiply(Fraction b) {
        int num = this.numerator * b.numerator;
        int den = this.denominator * b.denominator;
        return normalizeFraction(new Fraction(num, den));
    }

    public Fraction getMultiply(int b) {
        int num = this.numerator * b;
        return normalizeFraction(new Fraction(num, this.denominator));
    }

    public Fraction getDivide(Fraction b) {
        int num = this.numerator * b.denominator;
        int den = this.denominator * b.numerator;
        return normalizeFraction(new Fraction(num, den));
    }

    public Fraction getDivide(int b) {
        int den = this.denominator * b;
        return normalizeFraction(new Fraction(this.numerator, den));
    }

    public Fraction getFlippedFraction() {
        return new Fraction(this.denominator, this.numerator);
    }

    public String getFraction() {
        String str;
        if (this.numerator == 0)
            str = String.valueOf(this.numerator);
        else if (this.denominator == 1)
            str = String.valueOf(this.numerator);
        else
            str = this.numerator + "/" + this.denominator;
        return str;
    }

    public boolean isPositive() {
        return this.numerator >= 0;
    }

    public boolean isAbsMore(Fraction f) {
        return Math.abs(this.numerator * f.denominator) > Math.abs(f.numerator * this.denominator);
    }

    public boolean isMore(Fraction f){
        return this.numerator * f.denominator > f.numerator * this.denominator;
    }

    public boolean isLess(Fraction f){
        return this.numerator * f.denominator < f.numerator * this.denominator;
    }

    public double getDouble(){
        return (float)numerator/(float)denominator;
    }
}
