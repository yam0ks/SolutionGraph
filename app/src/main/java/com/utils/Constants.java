package com.utils;

public class Constants {
    public enum Sign {
        EQUALS,
        MORE,
        LESS
    }

    public enum GoalType {
        MAXIMIZE,
        MINIMIZE
    }

    public static final String ERROR_COLOR = "#ffa1a1";

    public static final Float GRAPH_OFFSET = 10F;
    public static final int MAX_RESTRICTIONS_NUMBER = 5;
    public static final int MAX_VARIABLES_NUMBER = 5;
}
