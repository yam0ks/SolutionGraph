package com.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;

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

    private Float right_bound = -1 * Float.MIN_VALUE; //Наибольшее значение среди точек по оси X
    private Float left_bound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси X
    private Float top_bound = -1 * Float.MIN_VALUE; //Наибольшее значение среди точек по оси Y
    private Float bottom_bound = Float.MAX_VALUE; //Наименьшее значение среди точек по оси Y

    public GraphSolver(){}

    public static class OutputData{ //Класс для представления результата работы графического метода
        public enum ErrorType{ //Перечисление с информацией о типе ошибке или ее отсутствии
            NOERROR,
            UNLIMITED,
            NOSOLUTION,
            UNKOWN,
            INCORECTDATA
        }

        public List<Expression> expressions; //Список полученных функций
        public Float right_bound;
        public Float left_bound;
        public Float top_bound;
        public Float bottom_bound;
        public Float x_solution; //X координата точки решения
        public Float y_solution; //Y координата точки решения
        public Float value_solution; //Результат оптимизации
        public ErrorType error; //Тип ошибки

        public OutputData(ErrorType type){
            expressions = new ArrayList<>();
            right_bound = 0F;
            left_bound = 0F;
            top_bound = 0F;
            bottom_bound = 0F;
            x_solution = 0F;
            y_solution = 0F;
            value_solution = 0F;
            error = type;
        }

        public OutputData(){
            expressions = new ArrayList<>();
            right_bound = 0F;
            left_bound = 0F;
            top_bound = 0F;
            bottom_bound = 0F;
            x_solution = 0F;
            y_solution = 0F;
            value_solution = 0F;
            error = ErrorType.NOERROR;
        }

        public void SetBounds(Float input_right_bound, Float input_left_bound, Float input_top_bound, Float input_bottom_bound){
            right_bound = input_right_bound;
            left_bound = input_left_bound;
            top_bound = input_top_bound;
            bottom_bound = input_bottom_bound;
        }

        public void SetSolution(double x_value, double y_value, double result_value){
            x_solution = (float)x_value;
            y_solution = (float)y_value;
            value_solution = (float)result_value;
        }

        public void SetExpressions(List<Expression> input_expressions){
            expressions = input_expressions;
        }

        public void SetError(ErrorType type){
            error = type;
        }

        public static class Expression{ //Класс для представления функций
            public enum Type { //Перечисление с информацией о типе графика (обычный, целевая функция, искусственная пристройка)
                DEFAULT,
                ARTIFICIAL,
                OBJECTIVE
            }

            public List<Entry> points; //Список с точками графика
            public GraphRestriction.Sign sign; //Знак ограничения
            public String str_expression; //Строковое представление выражения
            public Type type; //Тип графика

            public Expression(List<Entry> input_points, GraphRestriction.Sign s, String input_str_expression, Type input_type){
                points = input_points;
                sign = s;
                str_expression = input_str_expression;
                type = input_type;
            }

            public Expression(List<Entry> input_points, String input_str_expression, Type input_type){
                points = input_points;
                str_expression = input_str_expression;
                type = input_type;
                sign = GraphRestriction.Sign.EQ;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public OutputData CalculateGraph(List<GraphRestriction> input_restrictions, GraphObjective input_main_func){ //Основной метод для расчета всех значений

        restrictions = input_restrictions;
        objective = input_main_func;

        List<OutputData.Expression> expressions = new ArrayList<>();

        OutputData.ErrorType error = OutputData.ErrorType.NOERROR;

        OutputData data = new OutputData();

        try {
            expressions = CalculateExpressions();
            PointValuePair solution = Optimize();
            expressions.add(MakeObjectiveExpression(solution));
            data.SetSolution(solution.getPoint()[0], solution.getPoint()[1], solution.getValue());
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
            return new OutputData(OutputData.ErrorType.INCORECTDATA);
        }

        data.SetBounds(right_bound, left_bound, top_bound, bottom_bound);
        data.SetExpressions(expressions);
        data.SetError(error);

        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<OutputData.Expression> CalculateExpressions(){ //Расчет точек для всех графиков
        List<OutputData.Expression> inters = CalculateIntersections();

        if(inters.isEmpty())
            MakeExpressions(inters);

        MakeOffsets();

        List<OutputData.Expression> result = new ArrayList<>();

        for(int i = 0; i < inters.size(); ++i){
            if(restrictions.get(i).y_coeff == 0){
                Float restr_min_x = restrictions.get(i).CalculateX(bottom_bound);
                inters.get(i).points.add(0, new Entry(restr_min_x, bottom_bound));

                Float restr_max_x = restrictions.get(i).CalculateX(top_bound);
                inters.get(i).points.add(new Entry(restr_max_x, top_bound));

                result.add(inters.get(i));
                result.add(MakeArtificialExpression(inters.get(i)));
            }
            else{
                Float restr_min_y = restrictions.get(i).CalculateY(left_bound);
                inters.get(i).points.add(0, new Entry(left_bound, restr_min_y));

                Float restr_max_y =  restrictions.get(i).CalculateY(right_bound);
                inters.get(i).points.add(new Entry(right_bound, restr_max_y));

                result.add(inters.get(i));
            }
        }

        return  result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Object CalculateIntersectionPoint(GraphRestriction lhs, GraphRestriction rhs) { //Расчет точки пересечения двух графиков
        float denominator = lhs.x_coeff * rhs.y_coeff - rhs.x_coeff * lhs.y_coeff;

        if(denominator == 0)
            return false;

        float x = (lhs.y_coeff * -rhs.result_coeff - rhs.y_coeff * -lhs.result_coeff) / denominator;
        float y = (rhs.x_coeff * -lhs.result_coeff - lhs.x_coeff * -rhs.result_coeff) / denominator;

        UpdateBounds(x, y);

        return new Entry(x, y);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<OutputData.Expression> CalculateIntersections(){ //Расчет всех точек пересечения грфиков
        List<OutputData.Expression> result = new ArrayList<>();

        for (GraphRestriction restriction : restrictions) {
            List<Entry> cur_restriction = new ArrayList<>();
            for (GraphRestriction graphRestriction : restrictions) {
                Object intet_point_result = CalculateIntersectionPoint(restriction, graphRestriction);

                if (intet_point_result instanceof Entry)
                    cur_restriction.add((Entry) intet_point_result);
            }
            if (cur_restriction.isEmpty())
                continue;

            Collections.sort(cur_restriction, new CoordinatesComprator());
            result.add(new OutputData.Expression(cur_restriction, restriction.sign, restriction.string_expression, OutputData.Expression.Type.DEFAULT));
        }
        return result;
    }

    private void UpdateBounds(Float x_value, Float y_value){ //Обновление крайних значений по всем осям
        if(x_value > right_bound)
            right_bound = x_value;
        if(x_value < left_bound)
            left_bound = x_value;
        if(y_value > top_bound)
            top_bound = y_value;
        if(y_value < bottom_bound)
            bottom_bound = y_value;
    }

    private OutputData.Expression MakeArtificialExpression(OutputData.Expression line_data){ //Создание искусственной прямой
        List<Entry> r_points = new ArrayList<>();

        float x_coord = line_data.points.get(0).getX();
        float y_coord = line_data.points.get(0).getY();

        r_points.add(new Entry(x_coord, y_coord));

        if(line_data.sign == GraphRestriction.Sign.GEQ)
            r_points.add(new Entry(right_bound, y_coord));
        else
            r_points.add(new Entry(left_bound, y_coord));

        return new OutputData.Expression(r_points, line_data.sign, "", OutputData.Expression.Type.ARTIFICIAL);
    }

    private void MakeOffsets(){ //Добавление отступов к крайним значениям
        right_bound += 10F;
        left_bound -= 10F;
        top_bound += 10F;
        bottom_bound -= 10F;
    }

    private void MakeExpressions(List<OutputData.Expression> expressions){ //Создание графиков при параллельных прямых
        for (GraphRestriction restriction : restrictions) {
            List<Entry> points = new ArrayList<>();

            Float x = 0F;
            Float y = restriction.CalculateY(x);

            points.add(new Entry(x, y));
            UpdateBounds(x, y);
            expressions.add(new OutputData.Expression(points, restriction.sign, restriction.string_expression, OutputData.Expression.Type.DEFAULT));
        }
    }

    private PointValuePair Optimize(){ //Поиск решения
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { objective.x_coeff, objective.y_coeff}, 0);

        Collection<LinearConstraint> constraints = new ArrayList<>();

        for (GraphRestriction restriction : restrictions) {
            Relationship sign = (restriction.sign == GraphRestriction.Sign.GEQ) ? Relationship.GEQ : Relationship.LEQ;

            constraints.add(new LinearConstraint(new double[]{restriction.x_coeff, restriction.y_coeff}, sign, restriction.result_coeff));
        }

        GoalType type = (objective.goal_type == GraphObjective.GoalType.MAXIMIZE) ? GoalType.MAXIMIZE : GoalType.MINIMIZE;

        return new SimplexSolver().optimize(f, new LinearConstraintSet(constraints), type);
    }

    private OutputData.Expression MakeObjectiveExpression(PointValuePair solution){ //Создание графика целевой функции
        List<Entry> points = new ArrayList<>();

        objective.SetR_coeff(solution.getValue().floatValue());
        objective.Normalize();

        if(objective.y_coeff == 0){
            points.add(new Entry(objective.CalculateX(bottom_bound), bottom_bound));
            points.add(new Entry(objective.CalculateX(top_bound), top_bound));
        }
        else{
            points.add(new Entry(left_bound, objective.CalculateY(left_bound)));
            points.add(new Entry(right_bound, objective.CalculateY(right_bound)));
        }

        return new OutputData.Expression(points, objective.string_expression, OutputData.Expression.Type.OBJECTIVE);
    }

    private static class CoordinatesComprator implements Comparator<Entry>{ //Компаратор для сравнения точек типа Entry
        @Override
        public int compare(Entry lhs, Entry rhs) {
            return Float.compare(lhs.getX(), rhs.getX());
        }
    }
}