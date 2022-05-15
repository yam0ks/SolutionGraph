package com.usecase;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;
import com.model.graphdata.GraphFunction;
import com.model.graphdata.GraphObjective;
import com.model.graphdata.GraphOutputData;
import com.model.graphdata.GraphRestriction;
import com.utils.Constants;

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

    private Float rightBound = -1 * Float.MAX_VALUE; //Наибольшее значение среди точек по оси X
    private Float leftBound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси X
    private Float topBound = -1 * Float.MAX_VALUE; //Наибольшее значение среди точек по оси Y
    private Float bottomBound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси Y

    public GraphSolver(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GraphOutputData calculateGraphOutputData(List<GraphRestriction> inputRestrictions,
                                                    GraphObjective inputMainFunc){ // Основной метод для
                                                                              // расчета всех значений
        restrictions = uniqueRestrictions(inputRestrictions);
        objective = inputMainFunc;

        List<GraphFunction> expressions = new ArrayList<>();

        GraphOutputData.ErrorType error = GraphOutputData.ErrorType.NOERROR;

        GraphOutputData data = new GraphOutputData();

        try {
            expressions = calculateExpressions();
            PointValuePair solution = Optimize();
            expressions.add(makeObjectiveExpression(solution));
            data.setSolution(solution.getPoint()[0], solution.getPoint()[1], solution.getValue());
        }
        catch (TooManyIterationsException e){
            error = GraphOutputData.ErrorType.UNKOWN;
        }
        catch (UnboundedSolutionException e){
            error = GraphOutputData.ErrorType.UNLIMITED;
        }
        catch (NoFeasibleSolutionException e){
            error = GraphOutputData.ErrorType.NOSOLUTION;
        }
        catch (Exception e){
            return new GraphOutputData(GraphOutputData.ErrorType.INCORRECTDATA);
        }

        if(error == GraphOutputData.ErrorType.NOERROR){
            makeFinalBounds(expressions);
            hideNonVisibleGraphs(expressions);
        }

        data.setBounds(rightBound, leftBound, topBound, bottomBound);
        data.setExpressions(expressions);
        data.setError(error);

        setBoundsToDefault();

        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<GraphFunction> calculateExpressions(){ //Расчет точек для всех графиков
        List<GraphFunction> graphsIntersections = calculateIntersections();

        if(graphsIntersections.isEmpty())
            makeExpressions(graphsIntersections);

        makeOffsets();

        List<GraphFunction> result = new ArrayList<>();

        for(int i = 0; i < graphsIntersections.size(); ++i){
            if(restrictions.get(i).getYCoeff() == 0){
                Float restrMinX = restrictions.get(i).calculateX(bottomBound);
                graphsIntersections.get(i).getPoints().add(0, new Entry(restrMinX, bottomBound));

                Float restrMaxX = restrictions.get(i).calculateX(topBound);
                graphsIntersections.get(i).getPoints().add(new Entry(restrMaxX, topBound));

                result.add(graphsIntersections.get(i));

                if(graphsIntersections.get(i).getSign() != Constants.Sign.EQUALS)
                    result.add(makeArtificialExpression(graphsIntersections.get(i)));
            }
            else{
                Float restrMinY = restrictions.get(i).calculateY(leftBound);
                graphsIntersections.get(i).getPoints().add(0, new Entry(leftBound, restrMinY));

                Float restrMaxY =  restrictions.get(i).calculateY(rightBound);
                graphsIntersections.get(i).getPoints().add(new Entry(rightBound, restrMaxY));

                result.add(graphsIntersections.get(i));
            }
        }

        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object calculateIntersectionPoint(GraphRestriction leftHandSide,
                                              GraphRestriction rightHandSide) {
        float denominator = leftHandSide.getXCoeff() * rightHandSide.getYCoeff() - rightHandSide.getXCoeff()
                                                                        * leftHandSide.getYCoeff();

        if(denominator == 0)
            return false;

        float x = (leftHandSide.getYCoeff() * -rightHandSide.getResultCoeff() - rightHandSide.getYCoeff()
                                                * -leftHandSide.getResultCoeff()) / denominator;
        float y = (rightHandSide.getXCoeff() * -leftHandSide.getResultCoeff() - leftHandSide.getXCoeff()
                                              * -rightHandSide.getResultCoeff()) / denominator;

        updateBounds(x, y);

        return new Entry(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<GraphFunction> calculateIntersections(){ //Расчет всех точек пересечения графиков
        List<GraphFunction> result = new ArrayList<>();

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
            result.add(new GraphFunction(currentRestriction, restriction.getSign(),
                      restriction.getStringExpression(), GraphFunction.Type.DEFAULT));
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

    private GraphFunction makeArtificialExpression(GraphFunction lineData){ //Создание
                                                                               // искусственной прямой
        List<Entry> rPoints = new ArrayList<>();

        float xCoord = lineData.getPoints().get(0).getX();
        float yCoord = lineData.getPoints().get(0).getY();

        if(lineData.getSign() == Constants.Sign.MORE) {
            rPoints.add(new Entry(xCoord, yCoord));
            rPoints.add(new Entry(rightBound, yCoord));
        }
        else {
            rPoints.add(new Entry(leftBound, yCoord));
            rPoints.add(new Entry(xCoord, yCoord));
        }

        return new GraphFunction(rPoints, Constants.Sign.MORE, "",
                                GraphFunction.Type.ARTIFICIAL);
    }

    private void makeOffsets(){ //Добавление отступов к крайним значениям
        Constants.GRAPH_X_BOUNDS_OFFSET =  (leftBound == 0 && rightBound == 0) ? 10F :
                                        Math.abs(leftBound) + Math.abs(rightBound);
        Constants.GRAPH_Y_BOUNDS_OFFSET =  (bottomBound == 0 && topBound == 0) ? 10F :
                                        Math.abs(bottomBound) + Math.abs(topBound);

        rightBound += Constants.GRAPH_X_BOUNDS_OFFSET;
        leftBound -= Constants.GRAPH_X_BOUNDS_OFFSET;
        topBound += Constants.GRAPH_Y_BOUNDS_OFFSET;
        bottomBound -= Constants.GRAPH_Y_BOUNDS_OFFSET;
    }

    private void makeFinalBounds(List<GraphFunction> expressions){

        if(expressions.size() == 2 && expressions.get(1).getType() == GraphFunction.Type.OBJECTIVE){
            setBoundsToDefault();
            updateBounds(expressions.get(1).getPoints().get(1).getX(),
                         expressions.get(1).getPoints().get(1).getY());
            makeOffsets();
            return;
        }

        if(expressions.get(0).getType() == GraphFunction.Type.PARALLEL)
            return;

        setBoundsToDefault();

        for(GraphFunction expression : expressions){
            for(int i = 0; i < expression.getPoints().size(); ++i){
                if(i == 0 || i == expression.getPoints().size() - 1)
                    continue;
                Float x = expression.getPoints().get(i).getX();
                Float y = expression.getPoints().get(i).getY();
                if(checkRestrictionFulfillment(x, y))
                    updateBounds(x, y);
            }
        }

        makeOffsets();
    }

    private boolean checkRestrictionFulfillment(Float x, Float y){
        boolean result;

        for(GraphRestriction restriction : restrictions){
            result = restriction.checkConditionFulfillment(x, y);

            if(!result)
                return false;
        }
        return true;
    }

    private void hideNonVisibleGraphs(List<GraphFunction> expressions){
        for(GraphFunction function : expressions){
            if(function.getType() != GraphFunction.Type.DEFAULT)
                break;

            boolean visible = false;
            for(Entry point : function.getPoints()){
                float x = point.getX(); float y = point.getY();
                if(x > leftBound && x < rightBound &&
                        y > bottomBound && y < topBound) {
                    visible = true;
                    break;
                }
            }
            if(!visible)
                function.setType(GraphFunction.Type.ARTIFICIAL);
        }
    }

    private void makeExpressions(List<GraphFunction> expressions){ //Создание графиков при
                                                                           // параллельных прямых
        for (GraphRestriction restriction : restrictions) {
            List<Entry> points = new ArrayList<>();

            Float x;
            Float y;

            if(restriction.getYCoeff() == 0){
                y = 0F;
                x = restriction.calculateX(y);
            }
            else{
                x = 0F;
                y = restriction.calculateY(x);
            }

            points.add(new Entry(x, y));
            updateBounds(x, y);
            expressions.add(new GraphFunction(points, restriction.getSign(), restriction.getStringExpression(),
                    GraphFunction.Type.PARALLEL));
        }
    }

    private PointValuePair Optimize(){ //Поиск решения
        LinearObjectiveFunction objectiveFunc = new LinearObjectiveFunction(new double[]
                { objective.getXCoeff(), objective.getYCoeff()}, -objective.getResultCoeff());

        Collection<LinearConstraint> constraints = new ArrayList<>();

        for (GraphRestriction restriction : restrictions) {
            Relationship sign = Relationship.EQ;

            switch(restriction.getSign()){
                case EQUALS: sign = Relationship.EQ; break;
                case MORE: sign = Relationship.GEQ; break;
                case LESS: sign = Relationship.LEQ ; break;
            }

            constraints.add(new LinearConstraint(new double[]{restriction.getXCoeff(), restriction.getYCoeff()},
                                                 sign, restriction.getResultCoeff()));
        }

        GoalType type = (objective.getGoalType() == Constants.GoalType.MAXIMIZE) ? GoalType.MAXIMIZE :
                                                                               GoalType.MINIMIZE;

        return new SimplexSolver().optimize(objectiveFunc, new LinearConstraintSet(constraints), type);
    }

    private GraphFunction makeObjectiveExpression(PointValuePair solution){ //Создание графика целевой функции
        List<Entry> points = new ArrayList<>();

        objective.changeResultCoeff(solution.getValue().floatValue());
        objective.normalize();

        if(objective.getYCoeff() == 0){
            points.add(new Entry(objective.calculateX(bottomBound), bottomBound));
            points.add(new Entry((float)solution.getPoint()[0], (float)solution.getPoint()[1]));
            points.add(new Entry(objective.calculateX(topBound), topBound));
        }
        else{
            points.add(new Entry(leftBound, objective.calculateY(leftBound)));
            points.add(new Entry((float)solution.getPoint()[0], (float)solution.getPoint()[1]));
            points.add(new Entry(rightBound, objective.calculateY(rightBound)));
        }

        return new GraphFunction(points, objective.getStringExpression(),
                GraphFunction.Type.OBJECTIVE);
    }

    private void setBoundsToDefault(){
        leftBound = bottomBound = Float.MAX_VALUE;
        rightBound = topBound = -1 * Float.MAX_VALUE;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<GraphRestriction> uniqueRestrictions(List<GraphRestriction> restrictions){
        List<GraphRestriction> uniqueRestrictions = new ArrayList<>();

        for(GraphRestriction r : restrictions){
            GraphRestriction nonUniqueR = uniqueRestrictions.stream()
                    .filter(restrict -> restrict.getXCoeff().equals(r.getXCoeff()) &&
                                        restrict.getYCoeff().equals(r.getYCoeff()) &&
                                        restrict.getResultCoeff().equals(r.getResultCoeff()))
                    .findAny()
                    .orElse(null);

            if(nonUniqueR == null)
                uniqueRestrictions.add(r);
        }

        return uniqueRestrictions;
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