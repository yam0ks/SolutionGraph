package com.model.simplexdata;

import com.model.Fraction;

public class SimplexSolutionData { //раздел решения
    private Fraction[][] beforeMatrix; // симплекс матрица на начало текущего шага (0 строка - коэффициенты
    // при главной функции, последний столбец свободные коэффы b)
    private Fraction[][] afterMatrix; // симплекс матрица на конец текущего шага (0 строка - коэффициенты
    // при главной функции, последний столбец свободные коэффы b)
    private int[] bases;//базис в текущем шаге
    private boolean matrixCanBeSolved; //если false прекращается работа алгоритма, функция
    // не ограничена, решения нет
    private int supportColumn;//колонка опорного элемента
    private int supportRow;//строка опорного элемента
    private Fraction element;//значение опорного элемента
    private int oldBase;//номер старого базиса смещенного на -1
    private int newBase;//номер нового базиса смещенного на -1
    private Fraction[] simplexRelations;//Столбец симплекс-отношений Q (-1 - симлекс отношения нет)
    private boolean findMax;

    public SimplexSolutionData(){}

    public boolean getFindMax(){return findMax;}

    public void setFindMax(boolean findMax){this.findMax = findMax;}

    public Fraction[][] getBeforeMatrix() {
        return beforeMatrix;
    }

    public void setBeforeMatrix(Fraction[][] beforeMatrix) {
        this.beforeMatrix = beforeMatrix;
    }

    public Fraction[][] getAfterMatrix() {return afterMatrix;}

    public void setAfterMatrix(Fraction[][] afterMatrix) {this.afterMatrix = afterMatrix; }

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
        return oldBase + 1;
    }

    public void setOldBase(int oldBase) {
        this.oldBase = oldBase;
    }

    public int getNewBase() {
        return newBase + 1;
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
    public void setBases(int[] bases){this.bases = bases;}
    public int[] getBases(){return bases;}
}