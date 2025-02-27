package com.usecase;
import com.model.Fraction;
import com.model.Model;
import com.model.simplexdata.SimplexOutputData;
import com.model.simplexdata.SimplexInitData;
import com.model.simplexdata.SimplexNormalizeData;
import com.model.simplexdata.SimplexSolutionData;
import com.model.simplexdata.Section;
import com.model.simplexdata.MatrixItem;

import java.util.ArrayList;

public class SimplexParser {
    int currentSection;
    boolean fractionsLikeDouble;
    ArrayList<Section> sections;
    SimplexOutputData outputData;

    public void setData(SimplexOutputData outputData, boolean fractionsLikeDouble){
        this.outputData = outputData;
        this.fractionsLikeDouble = fractionsLikeDouble;
    }

    public Section[] getSections(){
        currentSection = 0;
        sections = new ArrayList<>();
        if(outputData.getAnswers() == null){
            setSection("Ответ", "Решения задачи не существует.");
        }
        else {
            formAnswer(outputData.getAnswers());
        }
        if(!formFirstSection())
            return getCopiedArray(sections);
        if(!formNormalizedSections()){
            return getCopiedArray(sections);
        }
        formSolutionSections();

        return getCopiedArray(sections);
    }

    private Section[] getCopiedArray(ArrayList<Section> sections){
        Section[] arraySections = new Section[sections.size()];
        for(int i = 0; i < arraySections.length; i++){
            arraySections[i] = sections.get(i);
        }
        return arraySections;
     }
    private int getStaticSections(){
        return outputData.getAnswers() == null ? 2 : 3;
    }

    private void formAnswer(Fraction[] answers){
        StringBuilder description = new StringBuilder();
        for(int i = 0; i < answers.length-1; i++){
            description.append(getIndexed("x", i)).append(" = ").append(answers[i].getFraction(fractionsLikeDouble)).append(", ");
        }
        description.append("F = ").append(answers[answers.length - 1].getFraction(fractionsLikeDouble));
        setSection("Ответ", description.toString());
    }

    private boolean formFirstSection(){
        SimplexInitData initData = outputData.getInitData();
        if(!initData.isCanBeSolved())
        {
            setSection("Решение задачи симплекс методом невозможно", "Количество ограничений, превышает количество возможных базисов.\n" +
                    "Существуют эквивалентные уравнения в текущей СЛАУ");
            return false;
        }
        if(initData.getChangedRowsSign().length != 0){
            setSection("Решение базовым симплекс методом",
                            "Меняем знаки у ограничений с ≥, путём умножения на -1. Для каждого ограничения с неравенством добавляем дополнительные переменные и сотставляем начальную симплекс таблицу. \nНачальная симплекс-таблица",
                    transformMatrix(initData.getMatrix(), initData.getBases(), false));
        }
        else{
            setSection("Решение базовым симплекс методом","Для каждого ограничения с неравенством добавляем дополнительные переменные и сотставляем начальную симплекс таблицу\n" +
                            "Начальная симплекс-таблица",
                    transformMatrix(initData.getMatrix(), initData.getBases(), false));
        }
        return true;
    }

    private boolean formNormalizedSections(){

        if(outputData.getNormalizeData().size() == 0)
            return true;
        setSection("Приведение симплекс таблицы к нормализованному виду", "Для продолжения работы алгоритма необходимо избавиться от отрицательных элементов в столбце b");
        int i = 1;
        for(SimplexNormalizeData normalizeData : outputData.getNormalizeData()){
            if(!normalizeData.getMatrixCanBeNormalized()){
                setSection("Итерация " + i, "Максимальное по модулю отрицательное b находится в строке " +
                        normalizeData.getOldBase() + ", но в данной строке отсутствуют отрицательные значения, значит решения задачи не существует");
                return false;
            }
            setSection("Итерация " + i, "Максимальное по модулю отрицательное b находится в строке " +
                            normalizeData.getOldBase() + " Максимальный по модулю отрицательный элемент в этой строке находится в столбце " +
                            normalizeData.getNewBase() + "Теперь делаем выбранный элемент базисным с помощью метода Гаусса",
                    transformMatrix(normalizeData.getMatrix(), normalizeData.getBases(), false));
            i++;
        }
        return true;
    }

    private void formSolutionSections(){
        if(outputData.getSolutionData().size() == 0){
            return;
        }

        setSection("Симплекс таблица с дельтами", "Расчитаем дельты и добавим их в нашу симплекс таблицу",
                transformMatrix(outputData.getSolutionData().get(0).getBeforeMatrix(),outputData.getSolutionData().get(0).getBases(),  true));
        boolean findMax = outputData.getSolutionData().get(outputData.getSolutionData().size() - 1).getFindMax();
        if(outputData.getSolutionData().size() == 1){
            String description;
            String neededSignDeltas;
            if(findMax)
                neededSignDeltas = "отрицательные дельты";
            else
                neededSignDeltas = "положительные дельты";


            description = "Проверяем план на оптимальность: план оптимален, так как отсутствуют " + neededSignDeltas;
            setSection("Анализ дельт", description);
        }
        outputData.getSolutionData().remove(0);

        int i = 1;
        for(SimplexSolutionData solutionData : outputData.getSolutionData()){
            if(solutionData.getMatrixCanBeSolved()){
                setSection("Итерация " + i, "Определяем разрешающий столбец - столбец, в котором находится минимальная дельта: " +
                                solutionData.getNewBase() + " " + getIndexed("Δ", solutionData.getNewBase()) + ": " +
                                solutionData.getBeforeMatrix()[solutionData.getBeforeMatrix().length - 1][solutionData.getNewBase()].getFraction(fractionsLikeDouble) + "\n" +
                                "Находим симплекс-отношения Q, путём деления коэффициентов b на соответствующие значения столбца " + solutionData.getNewBase() + "\n" +
                                "В найденном столбце ищем строку с наименьшим значением Q - строка " + solutionData.getSupportRow() +
                                "\nВ качестве базисной переменной строки " + solutionData.getSupportRow() + " берем " + getIndexed("x", solutionData.getNewBase()),
                        getMatrixWithSimplexRelations(transformMatrix(solutionData.getBeforeMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
                String neededSignDeltas;
                String description;
                if(solutionData.getFindMax())
                    neededSignDeltas = "отрицательные дельты";
                else
                    neededSignDeltas = "положительные дельты";
                if(i-1 != outputData.getSolutionData().size()-1){
                    description = "Проверяем план на оптимальность: план не оптимален, так как присутствуют " + neededSignDeltas;
                }
                else {
                    description = "Проверяем план на оптимальность: план оптимален, так как " + neededSignDeltas + " отсутствуют";
                }
                setSection("Симплекс-таблица с обновлёнными дельтами",description, getMatrixWithSimplexRelations(transformMatrix(solutionData.getAfterMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
            }
            else {
                setSection("Итерация " + i, "Все значения столбца "+ solutionData.getNewBase() + " неположительны.\n" +
                        "Функция не ограничена. Оптимальное решение отсутствует.", getMatrixWithSimplexRelations(transformMatrix(solutionData.getBeforeMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
            return;
            }
            i++;
        }
    }

    private String getDeltasFormula(int[] indBases){
        String formula = "";
        formula += getIndexed("Δ", "i") + " = ";
        for(int i = 0; i < indBases.length; i++){
            if(i == 0){
                formula += getIndexed("C", indBases[i] + "i");
            }
        }
        return formula;
    }

    private MatrixItem[][] getMatrixWithSimplexRelations(MatrixItem[][] matrix, int[] bases, Fraction[] simplexRelations){
        MatrixItem[][] matrixItems = new MatrixItem[matrix.length][matrix[0].length + 1];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length;j++){
                matrixItems[i][j] = new MatrixItem();
                matrixItems[i][j].value = matrix[i][j].value;
                matrixItems[i][j].isHeader = matrix[i][j].isHeader;
            }
        }
        matrixItems[0][matrixItems[0].length - 1] = new MatrixItem();
        matrixItems[0][matrixItems[0].length - 1].value = "-";
        matrixItems[0][matrixItems[0].length - 1].isHeader = false;
        matrixItems[1][matrixItems[0].length - 1] = new MatrixItem();
        matrixItems[1][matrixItems[0].length - 1].value = "Q";
        matrixItems[1][matrixItems[0].length - 1].isHeader = true;
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1] = new MatrixItem();
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1].value = "-";
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1].isHeader = false;
        for(int i = 2; i < matrix.length - 1; i++){
            if(!simplexRelations[i-2].isPositive()){
                matrixItems[i][matrixItems[0].length - 1] = new MatrixItem();
                matrixItems[i][matrixItems[0].length - 1].value = "-";
                matrixItems[i][matrixItems[0].length - 1].isHeader = false;
            }
            else {
                matrixItems[i][matrixItems[0].length - 1] = new MatrixItem();
                matrixItems[i][matrixItems[0].length - 1].value = simplexRelations[i-2].getFraction(fractionsLikeDouble);
                matrixItems[i][matrixItems[0].length - 1].isHeader = false;
            }
        }
        return matrixItems;
    }

    private MatrixItem[][] transformMatrix(Fraction[][] matrix, int[] bases, boolean includeDeltas){
        int matrixCorrection = 0;
        if(includeDeltas){
            matrixCorrection = 1;
        }
        MatrixItem[][] matrixItems = new MatrixItem[matrix.length+1][matrix[0].length + 1];
        matrixItems[0][0] = new MatrixItem();
        matrixItems[0][0].isHeader = true;
        matrixItems[0][0].value = "C";
        matrixItems[1][0] = new MatrixItem();
        matrixItems[1][0].isHeader = true;
        matrixItems[1][0].value = "базис";
        matrixItems[0][matrixItems[0].length-1] = new MatrixItem();
        matrixItems[0][matrixItems[0].length-1].isHeader = false;
        matrixItems[0][matrixItems[0].length-1].value = "0";
        for(int j = 1; j < matrix[0].length; j++){
            matrixItems[0][j] = new MatrixItem();
            matrixItems[0][j].value = matrix[0][j-1].getFraction(fractionsLikeDouble);
            matrixItems[0][j].isHeader = false;
            matrixItems[1][j] = new MatrixItem();
            matrixItems[1][j].value = getIndexed("x", j-1);
            matrixItems[1][j].isHeader = true;
        }
        matrixItems[1][matrixItems[0].length-1] = new MatrixItem();
        matrixItems[1][matrixItems[0].length-1].value = "b";
        matrixItems[1][matrixItems[0].length-1].isHeader = true;
        for(int i = 2; i < matrix.length - matrixCorrection + 1; i++){
            matrixItems[i][0] = new MatrixItem();
            matrixItems[i][0].value = getIndexed("x", bases[i-2]);
            matrixItems[i][0].isHeader = true;
        }
        if(includeDeltas){
            matrixItems[matrixItems.length-1][0] = new MatrixItem();
            matrixItems[matrixItems.length-1][0].value = "Δ";
            matrixItems[matrixItems.length-1][0].isHeader = true;
        }
        for(int i = 1; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                matrixItems[i+1][j+1] = new MatrixItem();
                matrixItems[i+1][j+1].value = matrix[i][j].getFraction(fractionsLikeDouble);
                matrixItems[i+1][j+1].isHeader = false;
            }
        }
        return matrixItems;
    }

    private String getIndexed(String letter, int index){
        return letter + index + 1;
    }

    private String getIndexed(String letter, String index){
        return letter + index;
    }

    private void setSection(String title, String description, MatrixItem[][] matrixItems){
        Section section = new Section();
        section.title = title;
        section.description = description;
        section.matrix = matrixItems;
        sections.add(section);
        currentSection++;
    }

    private void setSection(String title, String description){
        Section section = new Section();
        section.title = title;
        section.description = description;
        section.matrix = null;
        sections.add(section);
        currentSection++;
    }
}
