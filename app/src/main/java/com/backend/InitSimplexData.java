package com.backend;

public class InitSimplexData {
    public Fraction[][] matrix; // симплекс матрица на текущем шаге (0 строка - коэффициенты при главной функции, последний столбец свободные коэффы b)
    public int[] bases; //Номера базисов смещенных на -1 (x1 - 0, x4 - 3 и т.д.)
    public int[] changedRowsSign; //строки, знаки которых менялись было >= стало <=

    InitSimplexData(){}
}
