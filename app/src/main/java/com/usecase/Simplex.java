package com.usecase;

import com.model.Fraction;
import com.model.simplexdata.Objective;
import com.model.simplexdata.Restriction;
import com.model.simplexdata.SimplexInitData;
import com.model.simplexdata.SimplexNormalizeData;
import com.model.simplexdata.SimplexOutputData;
import com.model.simplexdata.SimplexSolutionData;
import com.utils.Constants;

import java.util.ArrayList;

public class Simplex {
    private Fraction[][] simplexMatrix;

    private int lastBasedRow = -1;
    private int lastBasedColumn = -1;
    private int[] indBases;
    private Fraction[] deltas;
    private Fraction[][] finalMatrix;
    private SimplexOutputData outputData;

    public Simplex() {}

    public SimplexOutputData getResult(Restriction[] restrictions, Objective objective) {
        this.outputData = new SimplexOutputData();
        this.outputData.setNormalizeData(new ArrayList<>());
        this.outputData.setSolutionData(new ArrayList<>());

        formSimplexMatrix(restrictions, objective);

        if(!normalizationMatrix()){
            return this.outputData;
        }
        findDelta(this.simplexMatrix);
        if(solutionMatrix(objective.getGoalType() == Constants.GoalType.MAXIMIZE)){
            outputData.setAnswers(getAnswers(objective));
            }
        return this.outputData;
    }

    private void formSimplexMatrix(Restriction[] restrictions, Objective objective){
        int countRestrictions = restrictions.length;
        int countVariables = restrictions[0].getFractionCoeffs().length;
        this.simplexMatrix = new Fraction[countRestrictions + 1][countRestrictions + countVariables
                                                                     - sumEquals(restrictions) + 1];
        this.outputData.setInitData(new SimplexInitData());
        this.outputData.setInitData(canonizeData(restrictions, this.outputData.getInitData()));
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            System.arraycopy(restrictions[i - 1].getFractionCoeffs(), 0, this.simplexMatrix[i], 0,
                                                        restrictions[i - 1].getFractionCoeffs().length);
            this.simplexMatrix[i][this.simplexMatrix[0].length - 1] = restrictions[i - 1].getResult();
        }
        for (int j = 0; j < this.simplexMatrix[0].length; j++){
            if(j < objective.getFractionCoeffs().length){
                this.simplexMatrix[0][j] = objective.getFractionCoeffs()[j];
            }
            else {
                this.simplexMatrix[0][j] = new Fraction(0);
            }
        }
        int[] bases = new int[countRestrictions];
        int biasBases = restrictions[0].getFractionCoeffs().length;
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            for(int j = biasBases; j < this.simplexMatrix[0].length - 1; j++) {
                simplexMatrix[i][j] = new Fraction(0);
            }
        }
        int countOfBases = 0;
        int countOfChosenBases = 0;
        for (int i = 1; i < this.simplexMatrix.length; i++) {
            if(restrictions[i-1].getSign() == Constants.Sign.EQUALS){
                int column = getBaseColumn(i, restrictions);
                transformMatrixByGauss(simplexMatrix, simplexMatrix[i][column], i, column);
                bases[countOfBases] = column;
                countOfBases++;
                countOfChosenBases++;
                continue;
            }
            for(int j = biasBases; j < this.simplexMatrix[0].length - 1; j++){
                if(j == biasBases + countOfBases - countOfChosenBases && restrictions[i - 1].getSign() != Constants.Sign.EQUALS) {
                    simplexMatrix[i][biasBases + countOfBases - countOfChosenBases] = new Fraction(1);
                    bases[countOfBases] = biasBases + countOfBases - countOfChosenBases;
                    countOfBases++;
                }
            }
        }
        indBases = bases;
        this.outputData.getInitData().setBases(indBases.clone());
        this.outputData.getInitData().setMatrix(simplexMatrix.clone());
    }

    private int sumEquals(Restriction[] restrictions) {
        int num = 0;
        for (Restriction restriction : restrictions)
            if (restriction.getSign() == Constants.Sign.EQUALS)
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
        for (int j = 0; j < restrictions[0].getFractionCoeffs().length; j++) {
            boolean zerosInColumn = true;

            if (this.simplexMatrix[row][j].getNumerator() == 1 && this.simplexMatrix[row][j].getDenominator() == 1)
                for (int i = 1; i < this.simplexMatrix.length; i++) {
                    if (i == row)
                        continue;
                    if (this.simplexMatrix[i][j].getNumerator() != 0) {
                        zerosInColumn = false;
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

    private int findColumnWithZeros(int row, Restriction[] restrictions) {
        int column = -1;
        for (int j = 0; j < restrictions[0].getFractionCoeffs().length; j++) {
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
        for (int j = 0; j < restrictions[0].getFractionCoeffs().length; j++) {
            if (this.simplexMatrix[row][j].getNumerator() != 0)
                return j;
        }
        return -1;
    }

    private SimplexInitData canonizeData(Restriction[] restrictions, SimplexInitData initSimplexData) {
        int[] changedRows = new int[sumMore(restrictions)];
        int k = 0;
        for (int i = 0; i < restrictions.length; i++)
            if (restrictions[i].getSign() == Constants.Sign.MORE){
                changedRows[k] = i + 1;
                k++;
                for (int j = 0; j < restrictions[i].getFractionCoeffs().length; j++) {
                    restrictions[i].getFractionCoeffs()[j] = restrictions[i].getFractionCoeffs()[j].getMultiply(-1);
                    restrictions[i].setSign(Constants.Sign.LESS);
                }
            }
        initSimplexData.setChangedRowsSign(changedRows);

        return initSimplexData;
    }

    private int sumMore(Restriction[] restrictions){
        int num = 0;
        for (Restriction restriction : restrictions) {
            if (restriction.getSign() == Constants.Sign.MORE)
                num += 1;
        }
        return num;
    }

    private boolean normalizationMatrix(){
        SimplexNormalizeData currentStep;
        boolean flag = true;
        while (isNegativeInLastColumn()){
            int row = getRowWithBiggestAmount();
            this.lastBasedRow = row - 1;
            if(isNegativeInRow(row)){
                int column = getColumnWithBiggestAmount(row);
                currentStep = new SimplexNormalizeData();
                currentStep.setOldBase(indBases[row - 1]);
                currentStep.setNewBase(column);
                indBases[row - 1] = column;
                currentStep.setSupportElementRow(row);
                currentStep.setSupportElementColumn(column);
                currentStep.setElement(this.simplexMatrix[row][column]);
                this.lastBasedColumn = column;
                transformMatrixByGauss(this.simplexMatrix, this.simplexMatrix[row][column],row,column);
                currentStep.setMatrix(copyFractionArray(simplexMatrix));
                currentStep.setMatrixCanBeNormalized(true);
                currentStep.setBases(indBases);
                this.outputData.getNormalizeData().add(currentStep);
            }
            else{
                currentStep = new SimplexNormalizeData();
                currentStep.setMatrix(copyFractionArray(simplexMatrix));
                currentStep.setMatrixCanBeNormalized(false);
                currentStep.setOldBase(row);
                this.outputData.getNormalizeData().add(currentStep);
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
        for(int j = 0; j < simplexMatrix[0].length - 1; j++){
            if(!simplexMatrix[row][j].isPositive())
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
        SimplexSolutionData currentStep = new SimplexSolutionData();
        currentStep.setBeforeMatrix(copyFractionArray(finalMatrix));
        currentStep.setBases(indBases);
        outputData.getSolutionData().add(currentStep);
        while (!deltaIsOk(findMax)){
            currentStep = new SimplexSolutionData();
            //currentStep.setMatrix(new Fraction[simplexMatrix.length+1][simplexMatrix[0].length]);
            int column = getSuitableColumn(findMax);
            currentStep.setSupportColumn(column);
            currentStep.setBeforeMatrix(copyFractionArray(finalMatrix));
            currentStep.setNewBase(column);
            currentStep.setBases(indBases);
            int row = getSuitableRow(column, currentStep);
            if(row != -1){
                currentStep.setSupportRow(row);
                currentStep.setElement(finalMatrix[row][column]);
                currentStep.setOldBase(indBases[row - 1]);
                currentStep.setNewBase(column);
                indBases[row - 1] = column;
                currentStep.setMatrixCanBeSolved(true);
                transformMatrixByGauss(finalMatrix, finalMatrix[row][column], row, column);
                currentStep.setAfterMatrix(copyFractionArray(finalMatrix));
                currentStep.setBases(indBases);
                currentStep.setFindMax(findMax);
                outputData.getSolutionData().add(currentStep);
                findDelta(finalMatrix);
            }
            else {
                currentStep.setFindMax(findMax);
                currentStep.setMatrixCanBeSolved(false);
                outputData.getSolutionData().add(currentStep);
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

    private int getSuitableRow(int column, SimplexSolutionData data){
        data.setSimplexRelations(new Fraction[finalMatrix.length-2]);
        int row = -1;
        Fraction minQ = new Fraction(-1);
        boolean firstElementIsFind = false;
        for(int i = 1; i < finalMatrix.length - 1; i++){
            if(!finalMatrix[i][column].isPositive() && finalMatrix[i][column].getNumerator() != 0)
            {
                data.setSimplexRelationsByIndex(i - 1, new Fraction(-1));
                continue;
            }
            if(!firstElementIsFind){
                minQ = finalMatrix[i][finalMatrix[0].length - 1].getDivide(finalMatrix[i][column]);
                data.setSimplexRelationsByIndex(i - 1, new Fraction(minQ));
                firstElementIsFind = true;
                row = i;
            }
            else {
                Fraction tmpQ = finalMatrix[i][finalMatrix[0].length - 1].getDivide(finalMatrix[i][column]);
                data.setSimplexRelationsByIndex(i - 1, new Fraction(tmpQ));
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

    private Fraction[][] copyFractionArray(Fraction[][] originalArray){
        Fraction[][] coppyedArray = new Fraction[originalArray.length][originalArray[0].length];
        for(int i = 0; i < originalArray.length; i++){
            for(int j = 0; j < originalArray[0].length; j++)
                coppyedArray[i][j] = new Fraction(originalArray[i][j]);
        }
        return coppyedArray;
    }

    private Fraction[] getAnswers(Objective objective){
        Fraction[] ans = new Fraction[objective.getFractionCoeffs().length + 1];
        for(int j = 0; j < objective.getFractionCoeffs().length + 1; j++){
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
                System.out.print(array[j].getFraction(false) + "\t\t\t");
            System.out.println();
        }
        System.out.println();
    }

    public void printArray(Fraction[] array){
        for(Fraction element : array)
            System.out.print(element.getFraction(false) + "\t");
    }
}
