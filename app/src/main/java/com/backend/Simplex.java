import java.util.ArrayList;

public class Simplex {
    private Fraction[][] simplexMatrix;
    private OutputData workingSteps;
    private InputData inputData;
    private int lastBasedRow = -1;
    private int lastBasedColumn = -1;
    private int[] indBases;
    private int[] changedRows;
    private Fraction[] deltas;
    private Fraction[][] finalMatrix;

    public enum Sign {
        MORE,
        LESS,
        EQUAL
    }

    public Simplex() {}

    public void SetInputData(InputData data){
        this.inputData = data;
    }

    public OutputData startWork() {
        this.workingSteps = new OutputData();
        workingSteps.normalizeData = new ArrayList<>();
        workingSteps.solutionData = new ArrayList<>();

        initSimplexMatrix();

        if(!normalizationMatrix()){
            return this.workingSteps;
        }
        findDelta(this.simplexMatrix);

        if(solutionMatrix()){
            workingSteps.setAnswers(getAnswers());
            printArray(getAnswers());
            return this.workingSteps;
        }
        else {
            return this.workingSteps;
        }
    }

    private void initSimplexMatrix() {
        int countRestrictions = this.inputData.restrictionsCoeff.length;
        int countVariables = this.inputData.restrictionsCoeff[0].length;
        this.simplexMatrix = new Fraction[countRestrictions + 1][countRestrictions + countVariables - sumEquals(inputData.comparisonSings)];
        InitSimplexData initSimplexData = new InitSimplexData();
        canonizeData();
        initRestrictionsCoeff();
        initMainFuncCoeff();
        initBases();
        initSimplexData.bases = indBases.clone();
        initSimplexData.matrix = this.simplexMatrix.clone();
        initSimplexData.changedRowsSign = this.changedRows.clone();
        workingSteps.setInitData(initSimplexData);
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
        NormalizeSimplexData current_step;
        boolean flag = true;
        while (isNegativeInLastColumn()){
            int row = getRowWithBiggestAmount();
            this.lastBasedRow = row - 1;
            if(isNegativeInRow(row)){
                int column = getColumnWithBiggestAmount(row);
                current_step = new NormalizeSimplexData();
                current_step.oldBase = indBases[row - 1];
                current_step.newBase = column;
                indBases[row - 1] = column;
                current_step.supportElementRow = row;
                current_step.supportElementColumn = column;
                current_step.element = this.simplexMatrix[row][column];
                this.lastBasedColumn = column;
                transformMatrixByGauss(this.simplexMatrix, this.simplexMatrix[row][column],row,column);
                current_step.matrix = this.simplexMatrix.clone();
                this.workingSteps.getNormalizeData().add(current_step);
                current_step.matrixCanBeNormalized = true;
                flag = true;
            }
            else{
                current_step = new NormalizeSimplexData();
                current_step.matrix = this.simplexMatrix.clone();
                current_step.matrixCanBeNormalized = false;
                this.workingSteps.getNormalizeData().add(current_step);
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
            SolutionSimplexData current_step = new SolutionSimplexData();
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
                workingSteps.getSolutionData().add(current_step);

            }
            else {
                current_step.matrixCanBeSolved = false;
                workingSteps.getSolutionData().add(current_step);
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

    private int getSuitableRow(int column, SolutionSimplexData data){
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
