import java.util.ArrayList;

public class OutputData {
    private InitSimplexData initData;
    private ArrayList<NormalizeSimplexData> normalizeData;
    private ArrayList<SolutionSimplexData> solutionData;
    private Fraction[] answers;

    OutputData()
    {
        normalizeData = new ArrayList<>();
        solutionData = new ArrayList<>();
    }
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
