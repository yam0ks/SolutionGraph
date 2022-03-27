package com.model;

import java.util.ArrayList;

public class Simplex {
    private Fraction[][] simplexMatrix;

    private int lastBasedRow = -1;
    private int lastBasedColumn = -1;
    private int[] indBases;
    private int[] changedRows;
    private Fraction[] deltas;
    private Fraction[][] finalMatrix;

    public static class InputData {
        public Fraction[][] restrictionsCoeff; //матрица коэффициентов ограничений(строка - одно уравнение)
        public Fraction[] mainFuncCoeff; //коэффициеты вычисляемой функции
        public Simplex.Sign[] comparisonSings; //столбик знаков неравенства задаются через enum Simplex.Sign
        public boolean findMax; //true если ищем максимум false если минимум

        public InputData() {}
    }


    public static class OutputData {
        public InitSimplexData initData; //Раздел Начальная симплекс матрийа
        public ArrayList<NormalizeSimplexData> normalizeData; //Раздел ищем начальное базисное решение
        public ArrayList<SolutionSimplexData> solutionData; //Раздел вычисляем дельты
        public Fraction[] answers; // Ответ от 0 до length-2 - коэффициенты при соответветсвующих иксах последний элемент - ответ

        OutputData(){}

        public InitSimplexData getInitData(){
            return initData;
        }
        public ArrayList<NormalizeSimplexData> getNormalizeData(){
            return normalizeData;
        }
        public ArrayList<SolutionSimplexData> getSolutionData(){ return solutionData; }
        public int[] getAnswers(int[] answers){ return answers; }
        public void setInitData(InitSimplexData initData){ this.initData = initData; }
        public void setAnswers(Fraction[] answers){ this.answers = answers; }

        public static class InitSimplexData { //первый раздел (исходная симплекс матрица)
            public Fraction[][] matrix; // симплекс матрица на текущем шаге (0 строка - коэффициенты при главной функции, последний столбец свободные коэффы b)
            public int[] bases; //Номера базисов смещенных на -1 (x1 - 0, x4 - 3 и т.д.)
            public int[] changedRowsSign; //строки, знаки которых менялись было >= стало <=

            InitSimplexData(){}
        }

        public static class NormalizeSimplexData { //второй раздел (приведение матрицы к каноническому виду)
            public Fraction[][] matrix;// симплекс матрица на текущем шаге (0 строка - коэффициенты при главной функции, последний столбец свободные коэффы b)
            public int oldBase;//номер старого базиса смещенного на -1
            public int newBase;//номер нового базиса смещенного на -1
            public int supportElementColumn;//колонка опорного элемента
            public int supportElementRow;//строка опорного элемента
            public Fraction element;//значение опорного элемента
            public boolean matrixCanBeNormalized;//если false прекращается работа алгоритма, функция не ограничена, решения нет

            NormalizeSimplexData(){}
        }

        public static class SolutionSimplexData { //раздел решения
            public Fraction[][] matrix; // симплекс матрица на текущем шаге (0 строка - коэффициенты при главной функции, последний столбец свободные коэффы b)
            public boolean matrixCanBeSolved; //если false прекращается работа алгоритма, функция не ограничена, решения нет
            public int supportColumn;//колонка опорного элемента
            public int supportRow;//строка опорного элемента
            public Fraction element;//значение опорного элемента
            public int oldBase;//номер старого базиса смещенного на -1
            public int newBase;//номер нового базиса смещенного на -1
            public Fraction[] SimplexRelations;//Столбец симплекс-отношений Q (-1 - симлекс отношения нет)

            SolutionSimplexData(){}
        }

    }

    private OutputData outputData;
    private InputData inputData;

    public Simplex() {}

    public void SetInputData(InputData data){
        this.inputData = data;
    }

    public OutputData getResult() {
        this.outputData = new OutputData();
        outputData.normalizeData = new ArrayList<>();
        outputData.solutionData = new ArrayList<>();

        initSimplexMatrix();
        if(!normalizationMatrix()){
            return this.outputData;
        }
        findDelta(this.simplexMatrix);

        if(solutionMatrix()){
            outputData.setAnswers(getAnswers());
            printMatrix(this.finalMatrix);
            return this.outputData;
        }
        else {
            return this.outputData;
        }
    }

    private void initSimplexMatrix() {
        int countRestrictions = this.inputData.restrictionsCoeff.length;
        int countVariables = this.inputData.restrictionsCoeff[0].length;
        this.simplexMatrix = new Fraction[countRestrictions + 1][countRestrictions + countVariables - sumEquals(inputData.comparisonSings)];
        Simplex.OutputData.InitSimplexData initSimplexData = new OutputData.InitSimplexData();
        canonizeData();
        initRestrictionsCoeff();
        initMainFuncCoeff();
        initBases();
        initSimplexData.bases = indBases.clone();
        initSimplexData.matrix = this.simplexMatrix.clone();
        initSimplexData.changedRowsSign = this.changedRows.clone();
        outputData.initData = new OutputData.InitSimplexData();
        outputData.initData = initSimplexData;
    }

    private void initRestrictionsCoeff() {
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            System.arraycopy(this.inputData.restrictionsCoeff[i - 1], 0, this.simplexMatrix[i], 0, this.inputData.restrictionsCoeff[0].length);
            this.simplexMatrix[i][this.simplexMatrix[0].length - 1] = this.inputData.restrictionsCoeff[i - 1][this.inputData.restrictionsCoeff[0].length - 1];
        }
    }

    private void initBases() { //test func
        int biasBases = this.inputData.restrictionsCoeff[0].length - 1;
        indBases = new int[this.inputData.restrictionsCoeff[0].length - 1];

        for (int i = 1; i < this.simplexMatrix.length; i++)
            for (int j = biasBases; j < this.simplexMatrix[0].length - 1; j++)
                if (i - 1 == j - biasBases) {
                    if (inputData.comparisonSings[j - biasBases] == Sign.EQUAL) {
                        int baseColumn = getBaseColumn(i);

                        indBases[j - biasBases] = baseColumn; //test
                        transformMatrixByGauss(this.simplexMatrix, this.simplexMatrix[i][baseColumn], baseColumn, i);

                    } else {
                        this.simplexMatrix[i][j] = new Fraction(1);
                        indBases[j - biasBases] = j;
                    }
                } else
                    this.simplexMatrix[i][j] = new Fraction(0);
    }

    private int getBaseColumn(int row) {
        if(findReadyBase(row) != -1)
            return findReadyBase(row);
        else
            if(findColumnWithZeros(row) != -1)
                return findColumnWithZeros(row);
            else
                return findFirstNoZerosColumn(row);
    }

    private int findReadyBase(int row) {
        int column = -1;
        for (int j = 0; j < this.inputData.restrictionsCoeff.length - 1; j++) {
            boolean zerosInColumn = true;

            if (this.simplexMatrix[row][j].getNumerator() == 1)
                for (int i = 1; i < this.inputData.restrictionsCoeff.length + 1; i++) {
                    if (i == row)
                        continue;
                    if (this.simplexMatrix[i][j].getNumerator() != 0) {
                        zerosInColumn = false;
                        break;
                    }
                }

            if (zerosInColumn)
                return j;
        }
        return column;
    }

    private int findColumnWithZeros(int row) {
        int column = -1;
        for (int j = 0; j < this.inputData.restrictionsCoeff.length - 1; j++) {
            boolean zerosInColumn = true;

            if (this.simplexMatrix[row][j].getNumerator() != 0)
                for (int i = 1; i < this.inputData.restrictionsCoeff.length + 1; i++) {
                    if (i == row)
                        continue;
                    if (this.simplexMatrix[i][j].getNumerator() != 0) {
                        zerosInColumn = false;
                        break;
                    }
                }

            if (zerosInColumn)
                return j;
        }
        return column;
    }

    private int findFirstNoZerosColumn(int row) {
        for (int j = 0; j < this.inputData.restrictionsCoeff.length - 1; j++) {
            if (this.simplexMatrix[row][j].getNumerator() != 0)
                return j;
        }
        return -1;
    }

    private void initMainFuncCoeff() {
        System.arraycopy(this.inputData.mainFuncCoeff, 0, this.simplexMatrix[0], 0, this.inputData.mainFuncCoeff.length);
        for (int j = this.inputData.mainFuncCoeff.length; j < this.simplexMatrix[0].length; j++)
            this.simplexMatrix[0][j] = new Fraction(0);
    }

    private void canonizeData() {
        changedRows = new int[sumMore(inputData.comparisonSings)];
        int k = 0;
        for (int i = 0; i < this.inputData.comparisonSings.length; i++)
            if (this.inputData.comparisonSings[i] == Sign.MORE){
                changedRows[k] = i;
                k++;
                for (int j = 0; j < this.inputData.restrictionsCoeff[0].length; j++) {
                    this.inputData.restrictionsCoeff[i][j] = this.inputData.restrictionsCoeff[i][j].getMultiply(-1);
                    this.inputData.comparisonSings[i] = Sign.LESS;
                }
            }
    }

    private int sumEquals(Sign[] signs) {
        int num = 0;
        for (Sign sign : signs)
            if (sign == Sign.EQUAL)
                num += 1;
        return num;
    }

    private int sumMore(Sign[] signs){
        int num = 0;
        for (Sign sign : signs)
            if (sign == Sign.MORE)
                num += 1;
        return num;
    }

    private boolean normalizationMatrix(){
        OutputData.NormalizeSimplexData current_step;
        boolean flag = true;
        while (isNegativeInLastColumn()){
            int row = getRowWithBiggestAmount();
            this.lastBasedRow = row - 1;
            if(isNegativeInRow(row)){
                int column = getColumnWithBiggestAmount(row);
                current_step = new OutputData.NormalizeSimplexData();
                current_step.oldBase = indBases[row - 1];
                current_step.newBase = column;
                indBases[row - 1] = column;
                current_step.supportElementRow = row;
                current_step.supportElementColumn = column;
                current_step.element = this.simplexMatrix[row][column];
                this.lastBasedColumn = column;
                transformMatrixByGauss(this.simplexMatrix, this.simplexMatrix[row][column],row,column);
                current_step.matrix = this.simplexMatrix.clone();
                this.outputData.getNormalizeData().add(current_step);
                current_step.matrixCanBeNormalized = true;
                flag = true;
            }
            else{
                current_step = new OutputData.NormalizeSimplexData();
                current_step.matrix = this.simplexMatrix.clone();
                current_step.matrixCanBeNormalized = false;
                this.outputData.getNormalizeData().add(current_step);
                flag = false;
                break;
            }
        }
        return flag;
    }

    private int getIndex(int[] array,int row){
        int ind = -1;
        for(int i = 0; i < array.length; i++){
            if(array[i] == row){
                ind = i;
            }
        }
        return ind;
    }

    private int getRowWithBiggestAmount(){
        Fraction[] tmpMatrix = new Fraction[this.simplexMatrix.length - 1];
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            tmpMatrix[i - 1] = this.simplexMatrix[i][this.simplexMatrix[0].length - 1];
        }
        return  getSuitableElement(tmpMatrix, lastBasedRow) + 1;
    }

    private int getColumnWithBiggestAmount(int row){
        Fraction[] tmpMatrix = new Fraction[this.simplexMatrix[0].length - 1];
        System.arraycopy(this.simplexMatrix[row], 0, tmpMatrix, 0, this.simplexMatrix[0].length - 1);
        return getSuitableElement(tmpMatrix, lastBasedColumn);
    }

    private boolean isNegativeInLastColumn() {
        for (Fraction[] rowArray : simplexMatrix) {
            if (!rowArray[simplexMatrix[0].length - 1].isPositive())
                return true;
        }
        return false;
    }

    private boolean isNegativeInRow(int row){
        for(Fraction element: this.simplexMatrix[row]){
            if(!element.isPositive())
                return true;
        }
        return false;
    }

    private int getSuitableElement(Fraction[] fractions, int lastInd) {
        int ind;
        if (lastInd != 0)
            ind = 0;
        else
            ind = 1;

        for (int i = 1; i < fractions.length; i++) {
            if (!fractions[ind].isAbsMore(fractions[i]) && lastInd != i && fractions[ind].getNumerator() < 0)
                ind = i;
        }
        return ind;
    }

    private boolean solutionMatrix(){
        finalMatrix = new Fraction[simplexMatrix.length+1][simplexMatrix[0].length];
        findDelta(simplexMatrix);
        for(int i = 0; i < simplexMatrix.length; i++)
            System.arraycopy(simplexMatrix[i], 0, finalMatrix[i], 0, simplexMatrix[0].length);
        System.arraycopy(deltas, 0, finalMatrix[finalMatrix.length - 1], 0, deltas.length);
        while (!deltaIsOk()){
            OutputData.SolutionSimplexData current_step = new OutputData.SolutionSimplexData();
            current_step.matrix = new Fraction[simplexMatrix.length+1][simplexMatrix[0].length];
            findDelta(finalMatrix);
            int column = getSuitableColumn();
            current_step.supportColumn = column;
            int row = getSuitableRow(column, current_step);
            if(row != -1){
                current_step.supportRow = row;
                current_step.element = finalMatrix[row][column];
                current_step.oldBase = indBases[row - 1];
                current_step.newBase = column;
                indBases[row - 1] = column;
                current_step.matrixCanBeSolved = true;
                transformMatrixByGauss(finalMatrix, finalMatrix[row][column], row, column);
                current_step.matrix = finalMatrix.clone();
                outputData.getSolutionData().add(current_step);

            }
            else {
                current_step.matrixCanBeSolved = false;
                outputData.getSolutionData().add(current_step);
                return false;
            }
        };
        return true;
    }

    private void findDelta(Fraction [][] matrix) {
        deltas = new Fraction[matrix[0].length];
        for(int j = 0; j < matrix[0].length; j++){
            deltas[j] = new Fraction(0);
            int row = 1;
            for (int indBase : this.indBases) {
                deltas[j] = deltas[j].getSum(matrix[0][indBase].getMultiply(matrix[row][j]));
                row++;
            }
            deltas[j] = deltas[j].getMinus(matrix[0][j]);
        }
    }

    private boolean deltaIsOk() {
        boolean deltaIsOk = true;
        if(inputData.findMax) {
            for (int i = 0; i < deltas.length-1; i++)
                if (!deltas[i].isPositive() && deltas[i].getNumerator() != 0)
                    deltaIsOk = false;
        }
        else {
            for (int i = 0; i < deltas.length-1; i++)
                if (deltas[i].isPositive() && deltas[i].getNumerator() != 0)
                    deltaIsOk = false;
        }
        return deltaIsOk;
    }

    private int getSuitableColumn(){
        int ind = 0;
        if(!inputData.findMax){
            Fraction max = deltas[0];
            for(int j = 1; j < deltas.length - 1; j++){
                if(deltas[j].isMore(max) && getIndex(indBases, j) == -1){
                    max = deltas[j];
                    ind = j;
                }
            }
        }
        else {
            Fraction min = deltas[0];
            for(int j = 1; j < deltas.length - 1; j++){
                if(deltas[j].isLess(min) && getIndex(indBases, j) == -1){
                    min = deltas[j];
                    ind = j;
                }
            }
        }
        return ind;
    }

    private int getSuitableRow(int column, OutputData.SolutionSimplexData data){
        data.SimplexRelations = new Fraction[finalMatrix.length-2];
        int row = -1;
        Fraction minQ = new Fraction(-1);
        boolean firstElementIsFind = false;
        for(int i = 1; i < finalMatrix.length - 1; i++){
            if(!finalMatrix[i][column].isPositive() && finalMatrix[i][column].getNumerator() != 0)
            {
                data.SimplexRelations[i - 1] = new Fraction(-1);
                continue;
            }
            if(!firstElementIsFind){
                minQ = finalMatrix[i][finalMatrix[0].length - 1].getDivide(finalMatrix[i][column]);
                data.SimplexRelations[i - 1] = new Fraction(minQ);
                firstElementIsFind = true;
                row = i;
            }
            else {
                Fraction tmpQ = finalMatrix[i][finalMatrix[0].length - 1].getDivide(finalMatrix[i][column]);
                data.SimplexRelations[i - 1] = new Fraction(tmpQ);
                if(minQ.isMore(tmpQ)){
                    minQ = tmpQ;
                    row = i;
                }
            }
        }
        return row;
    }

    private void transformMatrixByGauss(Fraction[][] simplexMatrix,Fraction element, int row, int column) {
        for (int j = 0; j < simplexMatrix[0].length; j++)
            simplexMatrix[row][j] = simplexMatrix[row][j].getDivide(element);

        for (int i = 1; i < simplexMatrix.length; i++) {
            Fraction factor = new Fraction(simplexMatrix[i][column]);
            for (int j = 0; j < simplexMatrix[0].length; j++) {
                if (i == row)
                    break;

                simplexMatrix[i][j] = simplexMatrix[i][j].getMinus(simplexMatrix[row][j].getMultiply(factor));
            }
        }
    }

    private Fraction[] getAnswers(){
        Fraction[] ans = new Fraction[inputData.restrictionsCoeff[0].length];
        for(int j = 0; j < inputData.restrictionsCoeff[0].length - 1; j++){
            if(getIndex(indBases, j) != -1){
                ans[j] = finalMatrix[getIndex(indBases, j) + 1][finalMatrix[0].length - 1];
            }
            else {
                ans[j] = new Fraction(0);
            }
        }
        ans[ans.length-1] = finalMatrix[finalMatrix.length - 1][finalMatrix[0].length - 1];
        return ans;
    }

    public void printMatrix(Fraction[][] matrix) {
        for (Fraction[] array : matrix) {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(array[j].getFraction() + "\t\t\t");
            System.out.println();
        }
        System.out.println();
    }

    public void printArray(Fraction[] array){
        for(Fraction element : array)
            System.out.print(element.getFraction() + "\t");
    }

}
