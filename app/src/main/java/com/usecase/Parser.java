package com.usecase;
import com.model.Fraction;
import com.model.Model;
import com.model.simplexdata.SimplexOutputData;
import com.model.simplexdata.SimplexInitData;
import com.model.simplexdata.SimplexNormalizeData;
import com.model.simplexdata.SimplexSolutionData;

public class Parser {
    int currentSection;
    boolean fractionsLikeDouble;
    Model.Section[] sections;
    SimplexOutputData outputData;

    public void setData(SimplexOutputData outputData, boolean fractionsLikeDouble){
        this.outputData = outputData;
        this.fractionsLikeDouble = fractionsLikeDouble;
    }

    public Model.Section[] formData(){
        currentSection = 0;
        sections = new Model.Section[2 + outputData.getNormalizeData().size() + outputData.getSolutionData().size() * 2];
        if(outputData.getAnswers() == null){
            setSection("Ответ", "Решения задачи не существует.");
        }
        else {
            formAnswer(outputData.getAnswers());
        }
        formFirstSection();
        if(!formNormalizedSections()){
            return sections;
        }
        formSolutionSections();

        return sections;
    }

    private void formAnswer(Fraction[] answers){
        StringBuilder description = new StringBuilder();
        for(int i = 0; i < answers.length-1; i++){
            description.append(getIndexed("x", i)).append(" = ").append(answers[i].getFraction(fractionsLikeDouble)).append(", ");
        }
        description.append("F = ").append(answers[answers.length - 1]);
        setSection("Ответ", description.toString());
    }

    private void formFirstSection(){
        SimplexInitData initData = outputData.getInitData();
        if(initData.getChangedRowsSign().length != 0){
            setSection("Решение базовым симплекс методом", """
                            Меняем знаки у ограничений с ≥, путём умножения на -1
                            Для каждого ограничения с неравенством добавляем дополнительные переменные и сотставляем начальную симплекс таблицу
                            Начальная симплекс-таблица""",
                    transformMatrix(initData.getMatrix(), initData.getBases(), false));
        }
        else{
            setSection("Решение базовым симплекс методом","Для каждого ограничения с неравенством добавляем дополнительные переменные и сотставляем начальную симплекс таблицу\n" +
                            "Начальная симплекс-таблица",
                    transformMatrix(initData.getMatrix(), initData.getBases(), false));
        }
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
        int i = 1;
        for(SimplexSolutionData solutionData : outputData.getSolutionData()){
            if(solutionData.getMatrixCanBeSolved()){
                setSection("Итерация " + i, "Определяем разрешающий столбец - столбец, в котором находится минимальная дельта: " +
                                solutionData.getNewBase() + " " + getIndexed("Δ", solutionData.getNewBase()) + ": " +
                                solutionData.getBeforeMatrix()[solutionData.getBeforeMatrix().length - 1][solutionData.getNewBase()] + "\n" +
                                "Находим симплекс-отношения Q, путём деления коэффициентов b на соответствующие значения столбца " + solutionData.getNewBase() + "\n" +
                                "В найденном столбце ищем строку с наименьшим значением Q - строка " + solutionData.getSupportRow() +
                                "В качестве базисной переменной строки " + solutionData.getSupportRow() + " берем " + getIndexed("x", solutionData.getNewBase()),
                        getMatrixWithSimplexRelations(transformMatrix(solutionData.getBeforeMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
                String description;
                String neededSignDeltas;
                if(solutionData.getFindMax())
                    neededSignDeltas = "отрицательные дельты";
                else
                    neededSignDeltas = "положительные дельты";
                if(i-1 != outputData.getSolutionData().size()-1){
                    description = "Проверяем план на оптимальность: план не оптимален, так как присутствуют " + neededSignDeltas;
                }
                else {
                    description = "Проверяем план на оптимальность: план не оптимален, так как отсутствуют " + neededSignDeltas;
                }
                setSection("Симплекс-таблица с обновлёнными дельтами",description, getMatrixWithSimplexRelations(transformMatrix(solutionData.getAfterMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
            }
            else {
                setSection("Итерация " + i, "Все значения столбца "+ solutionData.getNewBase() + " неположительны.\n" +
                        "Функция не ограничена. Оптимальное решение отсутствует.", getMatrixWithSimplexRelations(transformMatrix(solutionData.getBeforeMatrix(), solutionData.getBases(), true), solutionData.getBases(), solutionData.getSimplexRelations()));
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

    private Model.MatrixItem[][] getMatrixWithSimplexRelations(Model.MatrixItem[][] matrix, int[] bases, Fraction[] simplexRelations){
        Model.MatrixItem[][] matrixItems = new Model.MatrixItem[matrix.length][matrix[0].length + 1];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length;j++){
                matrixItems[i][j] = new Model.MatrixItem();
                matrixItems[i][j].value = matrix[i][j].value;
                matrixItems[i][j].isHeader = matrix[i][j].isHeader;
            }
        }
        matrixItems[0][matrixItems[0].length - 1] = new Model.MatrixItem();
        matrixItems[0][matrixItems[0].length - 1].value = "-";
        matrixItems[0][matrixItems[0].length - 1].isHeader = false;
        matrixItems[1][matrixItems[0].length - 1] = new Model.MatrixItem();
        matrixItems[1][matrixItems[0].length - 1].value = "Q";
        matrixItems[1][matrixItems[0].length - 1].isHeader = true;
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1] = new Model.MatrixItem();
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1].value = "-";
        matrixItems[matrixItems.length-1][matrixItems[0].length - 1].isHeader = false;
        for(int i = 2; i < matrix.length - 1; i++){
            if(!simplexRelations[i-2].isPositive()){
                matrixItems[i][matrixItems[0].length - 1] = new Model.MatrixItem();
                matrixItems[i][matrixItems[0].length - 1].value = "-";
                matrixItems[i][matrixItems[0].length - 1].isHeader = false;
            }
            else {
                matrixItems[i][matrixItems[0].length - 1] = new Model.MatrixItem();
                matrixItems[i][matrixItems[0].length - 1].value = simplexRelations[i-2].getFraction(fractionsLikeDouble);
                matrixItems[i][matrixItems[0].length - 1].isHeader = false;
            }
        }
        return matrixItems;
    }

    private Model.MatrixItem[][] transformMatrix(Fraction[][] matrix, int[] bases, boolean includeDeltas){
        int matrixCorrection = 0;
        if(includeDeltas){
            matrixCorrection = 1;
        }
        Model.MatrixItem[][] matrixItems = new Model.MatrixItem[matrix.length+1][matrix[0].length + 1];
        matrixItems[0][0] = new Model.MatrixItem();
        matrixItems[0][0].isHeader = true;
        matrixItems[0][0].value = "C";
        matrixItems[1][0] = new Model.MatrixItem();
        matrixItems[1][0].isHeader = true;
        matrixItems[1][0].value = "базис";
        matrixItems[0][matrixItems[0].length-1] = new Model.MatrixItem();
        matrixItems[0][matrixItems[0].length-1].isHeader = false;
        matrixItems[0][matrixItems[0].length-1].value = "0";
        for(int j = 1; j < matrix[0].length; j++){
            matrixItems[0][j] = new Model.MatrixItem();
            matrixItems[0][j].value = matrix[0][j-1].getFraction(fractionsLikeDouble);
            matrixItems[0][j].isHeader = false;
            matrixItems[1][j] = new Model.MatrixItem();
            matrixItems[1][j].value = getIndexed("x", j-1);
            matrixItems[1][j].isHeader = true;
        }
        matrixItems[1][matrixItems[0].length-1] = new Model.MatrixItem();
        matrixItems[1][matrixItems[0].length-1].value = "b";
        matrixItems[1][matrixItems[0].length-1].isHeader = true;
        for(int i = 2; i < matrix.length - matrixCorrection + 1; i++){
            matrixItems[i][0] = new Model.MatrixItem();
            matrixItems[i][0].value = getIndexed("x", bases[i-2]);
            matrixItems[i][0].isHeader = true;
        }
        if(includeDeltas){
            matrixItems[matrixItems.length-1][0] = new Model.MatrixItem();
            matrixItems[matrixItems.length-1][0].value = "Δ";
            matrixItems[matrixItems.length-1][0].isHeader = true;
        }
        for(int i = 1; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                matrixItems[i+1][j+1] = new Model.MatrixItem();
                matrixItems[i+1][j+1].value = matrix[i][j].getFraction(fractionsLikeDouble);
                matrixItems[i+1][j+1].isHeader = false;
            }
        }
        return matrixItems;
    }

    private String getIndexed(String letter, int index){
        return letter + (index + 1);
    }

    private String getIndexed(String letter, String index){
        return letter + index;
    }

    private void setSection(String title, String description, Model.MatrixItem[][] matrixItems){
        sections[currentSection] = new Model.Section();
        sections[currentSection].title = title;
        sections[currentSection].description = description;
        sections[currentSection].matrix = matrixItems;
        currentSection++;
    }

    private void setSection(String title, String description){
        sections[currentSection] = new Model.Section();
        sections[currentSection].title = title;
        sections[currentSection].description = description;
        sections[currentSection].matrix = null;
        currentSection++;
    }
}
