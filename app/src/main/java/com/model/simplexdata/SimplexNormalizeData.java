package com.model.simplexdata;

import com.model.Fraction;

public class SimplexNormalizeData { //второй раздел (приведение матрицы к
    // каноническому виду)
    private Fraction[][] matrix;// симплекс матрица на текущем шаге (0 строка - коэффициенты
    // при главной функции, последний столбец свободные коэффы b)
    private int[] bases;//базис в текущем шаге
    private int oldBase;//номер старого базиса смещенного на -1
    private int newBase;//номер нового базиса смещенного на -1
    private int supportElementColumn;//колонка опорного элемента
    private int supportElementRow;//строка опорного элемента
    private Fraction element;//значение опорного элемента
    private boolean matrixCanBeNormalized;//если false прекращается работа алгоритма,
    // функция не ограничена, решения нет
    public SimplexNormalizeData(){}

    public Fraction[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Fraction[][] matrix) {
        this.matrix = matrix;
    }

    public int getOldBase() {
        return oldBase;
    }

    public void setOldBase(int oldBase) {
        this.oldBase = oldBase;
    }

    public int getNewBase() {
        return newBase;
    }

    public void setNewBase(int newBase) {
        this.newBase = newBase;
    }

    public int getSupportElementColumn() {
        return supportElementColumn;
    }

    public void setSupportElementColumn(int supportElementColumn) {
        this.supportElementColumn = supportElementColumn;
    }

    public int getSupportElementRow() {
        return supportElementRow;
    }

    public void setSupportElementRow(int supportElementRow) {
        this.supportElementRow = supportElementRow;
    }

    public Fraction getElement() {
        return element;
    }

    public void setElement(Fraction element) {
        this.element = element;
    }

    public boolean getMatrixCanBeNormalized(){
        return matrixCanBeNormalized;
    }

    public void setMatrixCanBeNormalized(boolean matrixCanBeNormalized) {
        this.matrixCanBeNormalized = matrixCanBeNormalized;
    }
    public void setBases(int[] bases){this.bases = bases;}
    public int[] getBases(){return bases;}
}