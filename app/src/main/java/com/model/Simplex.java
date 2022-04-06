package com.model;

import java.util.ArrayList;

public class Simplex {
    private Fraction[][] simplexMatrix;

    private int lastBasedRow = -1;
    private int lastBasedColumn = -1;
    private int[] indBases;
    private Fraction[] deltas;
    private Fraction[][] finalMatrix;


    public static class OutputData {
        public InitSimplexData initData; //Раздел Начальная симплекс матрийа
        public ArrayList<NormalizeSimplexData> normalizeData; //Раздел ищем начальное базисное решение
        public ArrayList<SolutionSimplexData> solutionData; //Раздел вычисляем дельты
        public Fraction[] answers; // Ответ от 0 до length-2 - коэффициенты при соответветсвующих иксах последний элемент - ответ

        OutputData(){}
        private void setAnswers(Fraction[] answers){ this.answers = answers; }

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

    public Simplex() {}

    public OutputData getResult(Restriction[] restrictions, Objective objective) {
        this.outputData = new OutputData();
        outputData.normalizeData = new ArrayList<>();
        outputData.solutionData = new ArrayList<>();

        formSimplexMatrix(restrictions, objective);

        if(!normalizationMatrix()){
            return this.outputData;
        }
        findDelta(this.simplexMatrix);
        if(solutionMatrix(objective.goal_type == constants.GoalType.MAXIMIZE)){
            outputData.setAnswers(getAnswers(objective));
            }
        return this.outputData;
    }
    private void formSimplexMatrix(Restriction[] restrictions, Objective objective){
        int countRestrictions = restrictions.length;
        int countVariables = restrictions[0].coeffs.length;
        this.simplexMatrix = new Fraction[countRestrictions + 1][countRestrictions + countVariables - sumEquals(restrictions) + 1];
        this.outputData.initData = new OutputData.InitSimplexData();
        canonizeData(restrictions, this.outputData.initData);
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            System.arraycopy(restrictions[i-1].coeffs, 0, this.simplexMatrix[i], 0, restrictions[i-1].coeffs.length);
            this.simplexMatrix[i][this.simplexMatrix[0].length - 1] = restrictions[i-1].result;
        }
        for (int j = 0; j < this.simplexMatrix[0].length; j++){
            if(j < objective.coeffs.length){
                this.simplexMatrix[0][j] = objective.coeffs[j];
            }
            else {
                this.simplexMatrix[0][j] = new Fraction(0);
            }
        }
        int[] bases = new int[countVariables];
        int biasBases = restrictions[0].coeffs.length;
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            for(int j = biasBases; j < this.simplexMatrix[0].length - 1; j++){
                simplexMatrix[i][j] = new Fraction(0);
            }
        }
        int k = 0;
        int chosenBases = 0;
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            if(restrictions[i-1].sign == constants.Sign.EQ){
                int column = getBaseColumn(i, restrictions);
                transformMatrixByGauss(simplexMatrix, simplexMatrix[i][column], i, column);
                bases[k] = column;
                k++;
                chosenBases++;
                continue;
            }
            for(int j = biasBases; j < this.simplexMatrix[0].length; j++){
                if(j-biasBases == i - 1 && restrictions[i - 1].sign != constants.Sign.EQ) {
                    simplexMatrix[i][biasBases + k - chosenBases] = new Fraction(1);
                    bases[k] = j;
                    k++;
                }
            }
        }
        indBases = bases;
        this.outputData.initData.bases = indBases.clone();
        this.outputData.initData.matrix = simplexMatrix.clone();
    }

    private int sumEquals(Restriction[] restrictions) {
        int num = 0;
        for (Restriction restriction : restrictions)
            if (restriction.sign == constants.Sign.EQ)
                num += 1;
        return num;
    }

    private int getBaseColumn(int row, Restriction[] restrictions) {
        if(findReadyBase(row, restrictions) != -1)
            return findReadyBase(row, restrictions);
        else
            if(findColumnWithZeros(row, restrictions) != -1)
                return findColumnWithZeros(row, restrictions);
            else
                return findFirstNoZerosColumn(row, restrictions);
    }

    private int findReadyBase(int row, Restriction[] restrictions) {
        int column = -1;
        for (int j = 0; j < restrictions[0].coeffs.length; j++) {
            boolean zerosInColumn = true;

            if (this.simplexMatrix[row][j].getNumerator() == 1 && this.simplexMatrix[row][j].getDenominator() == 1)
                for (int i = 1; i < this.simplexMatrix.length; i++) {
                    if (i == row)
                        continue;
                    if (this.simplexMatrix[i][j].getNumerator() != 0) {
                        zerosInColumn = false;
                    }
                }
            else{
                zerosInColumn = false;
            }

            if (zerosInColumn)
                column = j;
        }
        return column;
    }

    private int findColumnWithZeros(int row, Restriction[] restrictions) {
        int column = -1;
        for (int j = 0; j < restrictions[0].coeffs.length; j++) {
            boolean zerosInColumn = true;

            if (this.simplexMatrix[row][j].getNumerator() != 0)
                for (int i = 1; i < this.simplexMatrix.length; i++) {
                    if (i == row)
                        continue;
                    if (this.simplexMatrix[i][j].getNumerator() != 0) {
                        break;
                    }
                }
            else{
                zerosInColumn = false;
            }

            if (zerosInColumn)
                column = j;
        }
        return column;
    }

    private int findFirstNoZerosColumn(int row, Restriction[] restrictions) {
        for (int j = 0; j < restrictions[0].coeffs.length; j++) {
            if (this.simplexMatrix[row][j].getNumerator() != 0)
                return j;
        }
        return -1;
    }

    private void canonizeData(Restriction[] restrictions, OutputData.InitSimplexData initSimplexData) {
        int[] changedRows = new int[sumMore(restrictions)];
        int k = 0;
        for (int i = 0; i < restrictions.length; i++)
            if (restrictions[i].sign == constants.Sign.GEQ){
                changedRows[k] = i + 1;
                k++;
                for (int j = 0; j < restrictions[i].coeffs.length; j++) {
                    restrictions[i].coeffs[j] = restrictions[i].coeffs[j].getMultiply(-1);
                    restrictions[i].sign = constants.Sign.LEQ;
                }
            }
        initSimplexData.changedRowsSign = changedRows;
    }

    private int sumMore(Restriction[] restrictions){
        int num = 0;
        for (Restriction restriction : restrictions) {
            if (restriction.sign == constants.Sign.GEQ)
                num += 1;
        }
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
                current_step.matrixCanBeNormalized = true;
                this.outputData.normalizeData.add(current_step);
            }
            else{
                current_step = new OutputData.NormalizeSimplexData();
                current_step.matrix = this.simplexMatrix.clone();
                current_step.matrixCanBeNormalized = false;
                this.outputData.normalizeData.add(current_step);
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

    private boolean solutionMatrix(boolean findMax){
        finalMatrix = new Fraction[simplexMatrix.length+1][simplexMatrix[0].length];
        findDelta(simplexMatrix);
        for(int i = 0; i < simplexMatrix.length; i++)
            System.arraycopy(simplexMatrix[i], 0, finalMatrix[i], 0, simplexMatrix[0].length);
        System.arraycopy(deltas, 0, finalMatrix[finalMatrix.length - 1], 0, deltas.length);
        while (!deltaIsOk(findMax)){
            OutputData.SolutionSimplexData current_step = new OutputData.SolutionSimplexData();
            current_step.matrix = new Fraction[simplexMatrix.length+1][simplexMatrix[0].length];
            int column = getSuitableColumn(findMax);
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
                outputData.solutionData.add(current_step);
                findDelta(finalMatrix);
            }
            else {
                current_step.matrixCanBeSolved = false;
                outputData.solutionData.add(current_step);
                return false;
            }
        }
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

    private boolean deltaIsOk(boolean findMax) {
        boolean deltaIsOk = true;
        if(findMax) {
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

    private int getSuitableColumn(boolean findMax){
        int ind = 0;
        if(!findMax){
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

    private Fraction[] getAnswers(Objective objective){
        Fraction[] ans = new Fraction[objective.coeffs.length + 1];
        for(int j = 0; j < objective.coeffs.length + 1; j++){
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
