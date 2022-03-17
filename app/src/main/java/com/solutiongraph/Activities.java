package com.solutiongraph;

public final class Activities {
    private static Activities instance;

    private int index = 0;
    private final int[] ACTIVITY = {
            R.layout.activity_main,
            R.layout.activity_second
    };
    public enum STEPS {
        COUNTS(1),
        COEFFS(2);

        private final int value;
        STEPS(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private Activities() {}

    public static Activities getInstance() {
        if (instance == null) {
            instance = new Activities();
        }
        return instance;
    }
    public boolean Next() {
        if (index + 1 >= ACTIVITY.length)
            return false;
        index++;
        return true;
    }
    public boolean Prev() {
        if (index - 1 < 0)
            return false;
        index--;
        return true;
    }
    public int Get() {
        return ACTIVITY[index];
    }
}