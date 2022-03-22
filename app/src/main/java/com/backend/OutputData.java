import java.util.ArrayList;

public class OutputData {
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
}
