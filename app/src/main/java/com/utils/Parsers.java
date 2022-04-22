package com.utils;

import android.annotation.SuppressLint;

import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;

public class Parsers {

    public static String parseXmlFromRestriction (Restriction restriction) {
        Double[] coeffs = restriction.getDoubleCoeffs();
        double freeCoeff = restriction.getDoubleFreeCoeff();
        Constants.Sign sign = restriction.getSign();
        double result = restriction.getResultAsDouble();

        String xml = parseXmlFromCoeffs(coeffs, freeCoeff);
        switch (sign) {
            case LESS:
                xml = xml.concat(" < ");
                break;
            case EQUALS:
                xml = xml.concat(" = ");
                break;
            case MORE:
                xml = xml.concat(" > ");
                break;
        }
        xml = xml.concat(stringFromNumber(result));
        return xml;
    }

    public static String parseXmlFromObjective (Objective objective) {
        Double[] coeffs = objective.getDoubleCoeffs();
        double freeCoeff = objective.getDoubleFreeCoeff();
        Constants.GoalType goal = objective.getGoalType();

        String xml = parseXmlFromCoeffs(coeffs, freeCoeff);
        xml += " ➝ ";
        switch (goal) {
            case MAXIMIZE:
                xml = xml.concat("max");
                break;
            case MINIMIZE:
                xml = xml.concat("min");
                break;
        }
        return xml;
    }

    @SuppressLint("DefaultLocale")
    private static String parseXmlFromCoeffs(Double[] coeffs, double freeCoeff) {
        boolean isFirst = false;
        String text = "";
        String template = "x<sub><small>%d</small></sub>";
        for (int i = 0; i < coeffs.length; i++) {
            double coeff = coeffs[i];
            if (coeff == 1.0) {
                text = text.concat(isFirst ? " + " : "")
                        .concat(String.format(template, i+1));
                isFirst = true;
            } else if (coeff > 0) {
                text = text.concat(isFirst ? " + " : "")
                        .concat(stringFromNumber(coeff))
                        .concat(String.format(template, i+1));
                isFirst = true;
            } else if (coeff < 0) {
                text = text.concat(" - ")
                        .concat(stringFromNumber(Math.abs(coeff)))
                        .concat(String.format(template, i+1));
                isFirst = true;
            }
        }
        if (freeCoeff != 0)
            text = text.concat(freeCoeff < 0 ? " - " : " + ")
                    .concat(stringFromNumber(Math.abs(freeCoeff)));
        return text;
    }

    public static String stringFromNumber(double number) {
        if (number % 1 == 0)
            return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }

    public static Double[] doublePrimitiveArrayToDoubleArray(double[] array) {
        int length = array.length;
        Double[] data = new Double[length];
        for (int i = 0; i < length; i++) {
            data[i] = array[i];
        }
        return data;
    }

    public static double[] doubleArrayToDoublePrimitiveArray(Double[] array) {
        int length = array.length;
        double[] data = new double[length];
        for (int i = 0; i < length; i++) {
            data[i] = array[i];
        }
        return data;
    }
}
