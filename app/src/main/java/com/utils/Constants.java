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

    public static Float GRAPH_X_BOUNDS_OFFSET;
    public static Float GRAPH_Y_BOUNDS_OFFSET;
    public static final int MAX_RESTRICTIONS_NUMBER = 5;
    public static final int MAX_VARIABLES_NUMBER = 5;
}
