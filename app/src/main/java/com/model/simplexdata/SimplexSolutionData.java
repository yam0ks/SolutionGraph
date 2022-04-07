package com.model.simplexdata;

import com.model.Fraction;

public class SimplexSolutionData { //раздел решения
    private Fraction[][] matrix; // симплекс матрица на текущем шаге (0 строка - коэффициенты
    // при главной функции, последний столбец свободные коэффы b)
    private boolean matrixCanBeSolved; //если false прекращается работа алгоритма, функция
    // не ограничена, решения нет
    private int supportColumn;//колонка опорного элемента
    private int supportRow;//строка опорного элемента
    private Fraction element;//значение опорного элемента
    private int oldBase;//номер старого базиса смещенного на -1
    private int newBase;//номер нового базиса смещенного на -1
    private Fraction[] simplexRelations;//Столбец симплекс-отношений Q (-1 - симлекс отношения нет)

    public SimplexSolutionData(){}

    public Fraction[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Fraction[][] matrix) {
        this.matrix = matrix;
    }

    public boolean getMatrixCanBeSolved(){
        return matrixCanBeSolved;
    }

    public void setMatrixCanBeSolved(boolean matrixCanBeSolved) {
        this.matrixCanBeSolved = matrixCanBeSolved;
    }

    public int getSupportColumn() {
        return supportColumn;
    }

    public void setSupportColumn(int supportColumn) {
        this.supportColumn = supportColumn;
    }

    public int getSupportRow() {
        return supportRow;
    }

    public void setSupportRow(int supportRow) {
        this.supportRow = supportRow;
    }

    public Fraction getElement() {
        return element;
    }

    public void setElement(Fraction element) {
        this.element = element;
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

    public Fraction[] getSimplexRelations() {
        return simplexRelations;
    }

    public void setSimplexRelations(Fraction[] simplexRelations) {
        this.simplexRelations = simplexRelations;
    }

    public void setSimplexRelationsByIndex(int idx, Fraction simplexRelation){
        this.simplexRelations[idx] = simplexRelation;
    }
}