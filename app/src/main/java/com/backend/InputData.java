package com.backend;

public class InputData {
    public Fraction[][] restrictionsCoeff; //матрица коэффициентов ограничений(строка - одно уравнение)
    public Fraction[] mainFuncCoeff; //коэффициеты вычисляемой функции
    public Simplex.Sign[] comparisonSings; //столбик знаков неравенства задаются через enum Simplex.Sign
    public boolean findMax; //true если ищем максимум false если минимум


    public InputData() {}
}
