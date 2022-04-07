package com.model;

import static com.model.constants.GRAPH_OFFSET;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;
import com.model.GraphObjective;
import com.model.GraphRestriction;
import com.model.constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.linear.UnboundedSolutionException;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

public class GraphSolver { //Ядро графического метода

    private  List<GraphRestriction> restrictions; //Список ограничений
    private GraphObjective objective; //Целевая функция

    private Float rightBound = -1 * Float.MIN_VALUE; //Наибольшее значение среди точек по оси X
    private Float leftBound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси X
    private Float topBound = -1 * Float.MIN_VALUE; //Наибольшее значение среди точек по оси Y
    private Float bottomBound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси Y

    public GraphSolver(){}

    public static class OutputData{ //Класс для представления результата работы графического метода
        public enum ErrorType{ //Перечисление с информацией о типе ошибке или ее отсутствии
            NOERROR,
            UNLIMITED,
            NOSOLUTION,
            UNKOWN,
            INCORRECTDATA
        }

        public List<Expression> expressions; //Список полученных функций
        public Float rightBound;
        public Float leftBound;
        public Float topBound;
        public Float bottomBound;
        public Float xSolution; //X координата точки решения
        public Float ySolution; //Y координата точки решения
        public Float valueSolution; //Результат оптимизации
        public ErrorType error; //Тип ошибки

        public OutputData(ErrorType type){
            expressions = new ArrayList<>();
            rightBound = 0F;
            leftBound = 0F;
            topBound = 0F;
            bottomBound = 0F;
            xSolution = 0F;
            ySolution = 0F;
            valueSolution = 0F;
            error = type;
        }

        public OutputData(){
            expressions = new ArrayList<>();
            rightBound = 0F;
            leftBound = 0F;
            topBound = 0F;
            bottomBound = 0F;
            xSolution = 0F;
            ySolution = 0F;
            valueSolution = 0F;
            error = ErrorType.NOERROR;
        }

        public void setBounds(Float inputRightBound, Float inputLeftBound, Float inputTopBound,
                                                                           Float inputBottomBound) {
            rightBound = inputRightBound;
            leftBound = inputLeftBound;
            topBound = inputTopBound;
            bottomBound = inputBottomBound;
        }

        public void setSolution(double xValue, double yValue, double resultValue) {
            xSolution = (float)xValue;
            ySolution = (float)yValue;
            valueSolution = (float)resultValue;
        }

        public void setExpressions(List<Expression> inputExpressions){
            expressions = inputExpressions;
        }

        public void setError(ErrorType type) {
            error = type;
        }

        public static class Expression{ //Класс для представления функций
            public enum Type { //Перечисление с информацией о типе графика (обычный, целевая функция,
                               // искусственная пристройка)
                DEFAULT,
                ARTIFICIAL,
                OBJECTIVE
            }

            public List<Entry> points; //Список с точками графика
            public constants.Sign sign; //Знак ограничения
            public String strExpression; //Строковое представление выражения
            public Type type; //Тип графика

            public Expression(List<Entry> inputPoints, constants.Sign sign, String inputStrExpression,
                                                                                     Type inputType) {
                points = inputPoints;
                this.sign = sign;
                strExpression = inputStrExpression;
                type = inputType;
            }

            public Expression(List<Entry> inputPoints, String inputStrExpression, Type inputType) {
                points = inputPoints;
                strExpression = inputStrExpression;
                type = inputType;
                sign = constants.Sign.EQUALS;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OutputData calculateGraphOutputData(List<GraphRestriction> inputRestrictions,
                                               GraphObjective inputMainFunc){ // Основной метод для
                                                                              // расчета всех значений

        restrictions = inputRestrictions;
        objective = inputMainFunc;

        List<OutputData.Expression> expressions = new ArrayList<>();

        OutputData.ErrorType error = OutputData.ErrorType.NOERROR;

        OutputData data = new OutputData();

        try {
            expressions = calculateExpressions();
            PointValuePair solution = Optimize();
            expressions.add(makeObjectiveExpression(solution));
            data.setSolution(solution.getPoint()[0], solution.getPoint()[1], solution.getValue());
        }
        catch (TooManyIterationsException e){
            error = OutputData.ErrorType.UNKOWN;
        }
        catch (UnboundedSolutionException e){
            error = OutputData.ErrorType.UNLIMITED;
        }
        catch (NoFeasibleSolutionException e){
            error = OutputData.ErrorType.NOSOLUTION;
        }
        catch (Exception e){
            return new OutputData(OutputData.ErrorType.INCORRECTDATA);
        }

        data.setBounds(rightBound, leftBound, topBound, bottomBound);
        data.setExpressions(expressions);
        data.setError(error);

        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<OutputData.Expression> calculateExpressions(){ //Расчет точек для всех графиков
        List<OutputData.Expression> graphsIntersections = calculateIntersections();

        if(graphsIntersections.isEmpty())
            makeExpressions(graphsIntersections);

        makeOffsets();

        List<OutputData.Expression> result = new ArrayList<>();

        for(int i = 0; i < graphsIntersections.size(); ++i){
            if(restrictions.get(i).yCoeff == 0){
                Float restrMinX = restrictions.get(i).calculateX(bottomBound);
                graphsIntersections.get(i).points.add(0, new Entry(restrMinX, bottomBound));

                Float restrMaxX = restrictions.get(i).calculateX(topBound);
                graphsIntersections.get(i).points.add(new Entry(restrMaxX, topBound));

                result.add(graphsIntersections.get(i));
                result.add(makeArtificialExpression(graphsIntersections.get(i)));
            }
            else{
                Float restrMinY = restrictions.get(i).calculateY(leftBound);
                graphsIntersections.get(i).points.add(0, new Entry(leftBound, restrMinY));

                Float restrMaxY =  restrictions.get(i).calculateY(rightBound);
                graphsIntersections.get(i).points.add(new Entry(rightBound, restrMaxY));

                result.add(graphsIntersections.get(i));
            }
        }

        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object calculateIntersectionPoint(GraphRestriction leftHandSide,
                                              GraphRestriction rightHandSide) {
        float denominator = leftHandSide.xCoeff * rightHandSide.yCoeff - rightHandSide.xCoeff
                                                                        * leftHandSide.yCoeff;

        if(denominator == 0)
            return false;

        float x = (leftHandSide.yCoeff * -rightHandSide.resultCoeff - rightHandSide.yCoeff
                                                * -leftHandSide.resultCoeff) / denominator;
        float y = (rightHandSide.xCoeff * -leftHandSide.resultCoeff - leftHandSide.xCoeff
                                              * -rightHandSide.resultCoeff) / denominator;

        updateBounds(x, y);

        return new Entry(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<OutputData.Expression> calculateIntersections(){ //Расчет всех точек пересечения графиков
        List<OutputData.Expression> result = new ArrayList<>();

        for (GraphRestriction restriction : restrictions) {
            List<Entry> currentRestriction = new ArrayList<>();
            for (GraphRestriction graphRestriction : restrictions) {
                Object intersectPointResult = calculateIntersectionPoint(restriction, graphRestriction);

                if (intersectPointResult instanceof Entry)
                    currentRestriction.add((Entry) intersectPointResult);
            }
            if (currentRestriction.isEmpty())
                continue;

            Collections.sort(currentRestriction, new CoordinatesComprator());
            result.add(new OutputData.Expression(currentRestriction, restriction.sign,
                      restriction.stringExpression, OutputData.Expression.Type.DEFAULT));
        }
        return result;
    }

    private void updateBounds(Float xValue, Float yValue){ //Обновление крайних значений по всем осям
        if(xValue > rightBound)
            rightBound = xValue;
        if(xValue < leftBound)
            leftBound = xValue;
        if(yValue > topBound)
            topBound = yValue;
        if(yValue < bottomBound)
            bottomBound = yValue;
    }

    private OutputData.Expression makeArtificialExpression(OutputData.Expression lineData){ //Создание
                                                                               // искусственной прямой
        List<Entry> rPoints = new ArrayList<>();

        float xCoord = lineData.points.get(0).getX();
        float yCoord = lineData.points.get(0).getY();

        rPoints.add(new Entry(xCoord, yCoord));

        if(lineData.sign == constants.Sign.MORE)
            rPoints.add(new Entry(rightBound, yCoord));
        else
            rPoints.add(new Entry(leftBound, yCoord));

        return new OutputData.Expression(rPoints, lineData.sign, "",
                                        OutputData.Expression.Type.ARTIFICIAL);
    }

    private void makeOffsets(){ //Добавление отступов к крайним значениям
        rightBound += GRAPH_OFFSET;
        leftBound -= GRAPH_OFFSET;
        topBound += GRAPH_OFFSET;
        bottomBound -= GRAPH_OFFSET;
    }

    private void makeExpressions(List<OutputData.Expression> expressions){ //Создание графиков при
                                                                           // параллельных прямых
        for (GraphRestriction restriction : restrictions) {
            List<Entry> points = new ArrayList<>();

            Float x = 0F;
            Float y = restriction.calculateY(x);

            points.add(new Entry(x, y));
            updateBounds(x, y);
            expressions.add(new OutputData.Expression(points, restriction.sign, restriction.stringExpression,
                           OutputData.Expression.Type.DEFAULT));
        }
    }

    private PointValuePair Optimize(){ //Поиск решения
        LinearObjectiveFunction objectiveFunc = new LinearObjectiveFunction(new double[]
                { objective.xCoeff, objective.yCoeff}, 0);

        Collection<LinearConstraint> constraints = new ArrayList<>();

        for (GraphRestriction restriction : restrictions) {
            Relationship sign = (restriction.sign == constants.Sign.MORE) ? Relationship.GEQ :
                                                                            Relationship.LEQ;

            constraints.add(new LinearConstraint(new double[]{restriction.xCoeff, restriction.yCoeff},
                                                 sign, restriction.resultCoeff));
        }

        GoalType type = (objective.goalType == constants.GoalType.MAXIMIZE) ? GoalType.MAXIMIZE :
                                                                               GoalType.MINIMIZE;

        return new SimplexSolver().optimize(objectiveFunc, new LinearConstraintSet(constraints), type);
    }

    private OutputData.Expression makeObjectiveExpression(PointValuePair solution){ //Создание графика целевой функции
        List<Entry> points = new ArrayList<>();

        objective.setResultCoeff(solution.getValue().floatValue());
        objective.Normalize();

        if(objective.yCoeff == 0){
            points.add(new Entry(objective.calculateX(bottomBound), bottomBound));
            points.add(new Entry(objective.calculateX(topBound), topBound));
        }
        else{
            points.add(new Entry(leftBound, objective.calculateY(leftBound)));
            points.add(new Entry(rightBound, objective.calculateY(rightBound)));
        }

        return new OutputData.Expression(points, objective.stringExpression,
                                        OutputData.Expression.Type.OBJECTIVE);
    }

    private static class CoordinatesComprator implements Comparator<Entry>{ // Компаратор для
                                                                            // сравнения точек
                                                                            // типа Entry
        @Override
        public int compare(Entry leftHandSide, Entry rightHandSide) {
            return Float.compare(leftHandSide.getX(), rightHandSide.getX());
        }
    }
}