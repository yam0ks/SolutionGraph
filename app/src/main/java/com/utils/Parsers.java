package com.utils;

import android.annotation.SuppressLint;

import com.model.simplexdata.Restriction;

public class Parsers {

    @SuppressLint("DefaultLocale")
    public static String parseXmlFromRestriction (Restriction restriction) {
        double[] coeffs = restriction.getDoubleCoeffs();
        double freeCoeff = restriction.getDoubleFreeCoeff();
        Constants.Sign sign = restriction.getSign();
        double result = restriction.getResultAsDouble();
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
        switch (sign) {
            case LESS:
                text = text.concat(" < ");
                break;
            case EQUALS:
                text = text.concat(" = ");
                break;
            case MORE:
                text = text.concat(" > ");
                break;
        }
        text = text.concat(stringFromNumber(result));
        return text;
    }

    public static String stringFromNumber(double number) {
        if (number % 1 == 0)
            return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }
}
