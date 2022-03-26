package com.backend;

public class NormalizeSimplexData {
    public Fraction[][] matrix;// симплекс матрица на текущем шаге (0 строка - коэффициенты при главной функции, последний столбец свободные коэффы b)
    public int oldBase;//номер старого базиса смещенного на -1
    public int newBase;//номер нового базиса смещенного на -1
    public int supportElementColumn;//колонка опорного элемента
    public int supportElementRow;//строка опорного элемента
    public Fraction element;//значение опорного элемента
    public boolean matrixCanBeNormalized;//если false прекращается работа алгоритма, функция не ограничена, решения нет

    NormalizeSimplexData(){}
}
