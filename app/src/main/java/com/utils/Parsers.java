package com.utils;

import android.annotation.SuppressLint;

import com.model.graphdata.GraphObjective;
import com.model.graphdata.GraphRestriction;

import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;

import java.util.ArrayList;
import java.util.List;

public class Parsers {

    @SuppressLint("DefaultLocale")
    public static String formatDoubleString(Double number) {
        if(number % 1 == 0)
            return String.format("%d", number.intValue());
        return number.toString();
    }

    @SuppressLint("DefaultLocale")
    private static String parseXmlFromCoeffs(String[] coeffs, String freeCoeff) {
        boolean isFirst = true;
        String text = "";
        String template = "x<sub><small>%d</small></sub>";
        for (int i = 0; i < coeffs.length; i++) {
            String coeff = coeffs[i];
            Double number = stringToDouble(coeff);
            if (number == null) {
                text = text.concat(isFirst ? "" : " + ")
                           .concat(coeff)
                           .concat(String.format(template, i+1));
                isFirst = false;
            } else if (number == 1) {
                text = text.concat(isFirst ? "" : " + ")
                           .concat(String.format(template, i+1));
            } else if (number > 0) {
                text = text.concat(isFirst ? "" : " + ")
                           .concat(coeff)
                           .concat(String.format(template, i+1));
            } else if (number < 0) {
                if (coeff.equals("-1")) {
                    text = text.concat(" - ")
                               .concat(String.format(template, i+1));
                } else {
                    text = text.concat(" - ")
                               .concat(formatDoubleString(Math.abs(number)))
                               .concat(String.format(template, i+1));
                }
            }
            if (number != null && number != 0) isFirst = false;
        }
        if (freeCoeff.equals("")) return text;
        Double fCoeff = stringToDouble(freeCoeff);
        if (fCoeff == null ) {
            text = text.concat(" + ").concat(freeCoeff);
        } else if (fCoeff != 0) {
            text = text.concat(fCoeff < 0 ? " - " : " + ")
                       .concat(formatDoubleString(Math.abs(fCoeff)));
        }
        return text;
    }

    public static String parseXmlFromRestriction (
            String[] coeffs,
            String freeCoeff,
            Constants.Sign sign,
            String result
    ) {
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
        xml = xml.concat(result);
        return xml;
    }

    public static String parseXmlFromObjective (
            String[] coeffs,
            String freeCoeff,
            Constants.GoalType goal
    ) {
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

    public static Double stringToDouble(String value) {
        try {
            return Double.parseDouble(value);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Double[] convertStringsToDoubles(String[] strings) {
        Double[] doubles = new Double[strings.length];
        for (int i = 0; i < strings.length; i++) {
            doubles[i] = stringToDouble(strings[i]);
        }
        return doubles;
    }

    public static String[] convertDoubleSToStrings(Double[] doubles) {
        String[] strings = new String[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            strings[i] = formatDoubleString(doubles[i]);
        }
        return strings;
    }

    public static Object toGraphRestriction(Restriction[] restrictions){

        List<GraphRestriction> result = new ArrayList<>();

        for(Restriction restriction : restrictions){
            Double[] coeffs = restriction.getDoubleCoeffs();

            if(coeffs.length != 2)
                return false;

            Float xValue = coeffs[0].floatValue();
            Float yValue = (coeffs.length == 1) ? 0 : coeffs[1].floatValue();

            Float resultValue = restriction.getResultAsDouble().floatValue() -
                                restriction.getDoubleFreeCoeff().floatValue();

            Constants.Sign sign = restriction.getSign();

            result.add(new GraphRestriction(xValue, yValue, sign, resultValue));
        }

        return result;
    }

    public static Object toGraphObjective(Objective objective){

        Double[] coeffs = objective.getDoubleCoeffs();

        if(coeffs.length != 2)
            return false;

        Float xValue = coeffs[0].floatValue();
        Float yValue = coeffs[1].floatValue();
        Constants.GoalType goal = objective.getGoalType();

        return new GraphObjective(xValue, yValue, -(float)objective.getFreeCoeff(), goal);
    }
}
