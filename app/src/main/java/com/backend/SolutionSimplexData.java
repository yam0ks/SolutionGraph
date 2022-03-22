public class SolutionSimplexData {

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
