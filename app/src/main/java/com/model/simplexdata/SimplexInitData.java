package com.model.simplexdata;

import com.model.Fraction;

public class SimplexInitData { //первый раздел (исходная симплекс матрица)
    private Fraction[][] matrix; // симплекс матрица на текущем шаге (0 строка - коэффициенты
    // при главной функции, последний столбец свободные коэффы b)
    private int[] bases; //Номера базисов смещенных на -1 (x1 - 0, x4 - 3 и т.д.)
    private int[] changedRowsSign; //строки, знаки которых менялись было >= стало <=

    public boolean isCanBeSolved() {
        return canBeSolved;
    }

    public void setCanBeSolved(boolean canBeSolved) {
        this.canBeSolved = canBeSolved;
    }

    private boolean canBeSolved = true;

    public SimplexInitData(){}

    public Fraction[][] getMatrix(){
        return matrix;
    }

    public void setMatrix(Fraction[][] matrix) {
        this.matrix = matrix;
    }

    public int[] getBases(){
        return bases;
    }

    public void setBases(int[] bases) {
        this.bases = bases;
    }

    public int[] getChangedRowsSign(){
        return changedRowsSign;
    }

    public void setChangedRowsSign(int[] changedRowsSign) {
        this.changedRowsSign = changedRowsSign;
    }
}