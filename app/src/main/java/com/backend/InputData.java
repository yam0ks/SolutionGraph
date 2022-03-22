public class InputData {
    public Fraction[][] restrictionsCoeff;
    public Fraction[] mainFuncCoeff;
    public Simplex.Sign[] comparisonSings;
    public boolean findMax;


    public InputData() {
    }

    public InputData(Fraction[][] restrictionsCoeff, Fraction[] mainFuncCoeff, Simplex.Sign[] comparisonSings, boolean findMax) {
        this.restrictionsCoeff = restrictionsCoeff;
        this.mainFuncCoeff = mainFuncCoeff;
        this.comparisonSings = comparisonSings;
        this.findMax = findMax;
    }
}
