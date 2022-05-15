package com.solutiongraph.steps;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.model.graphdata.GraphFunction;
import com.model.graphdata.GraphOutputData;
import com.solutiongraph.R;
import com.solutiongraph.graph.GraphMarkerView;
import com.utils.Constants;
import com.viewmodel.SharedViewModel;

import java.util.ArrayList;

public class GraphResultFragment extends Fragment {

    private int currentColor = -1;
    private SharedViewModel viewModel;
    private View root;

    public GraphResultFragment() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() == null) return;
            viewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        final Observer<GraphOutputData> graphOutputDataObserver = outputData -> {
            if(outputData != null)
                drawGraph(outputData);
        };

        viewModel.graphOutputData.observe(this, graphOutputDataObserver);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawGraph(GraphOutputData outputData){

        if(outputData.getError() == GraphOutputData.ErrorType.INCORRECTDATA) {
            showDialog("Неверные входные данные!");
            return;
        }

        LineChart chart = root.findViewById(R.id.chart);
        setChartProperties(chart);

        GraphMarkerView mv = new GraphMarkerView(this.getContext(), R.layout.graph_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        for(int i = 0; i < outputData.getExpressions().size(); ++i){
            LineDataSet set = new LineDataSet(outputData.getExpressions().get(i).getPoints(),
                                            outputData.getExpressions().get(i).getStrExpression());

            configureDataSetProperties(set, outputData, i);
            dataSets.add(set);
        }

        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);

        configureViewPort(chart, outputData);

        if(outputData.getError() != GraphOutputData.ErrorType.NOERROR)
            reportError(outputData.getError());

        setColorToDefault();
    }

    private void setChartProperties(LineChart chart){
        chart.getAxisRight().setEnabled(false); //убирает ось справа
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //помещает ось X вниз
        chart.getXAxis().setGridLineWidth(2f); //задает ширину сетки по оси X
        chart.getAxis(YAxis.AxisDependency.LEFT).setGridLineWidth(2f); //задает ширину сетки по оси Y
        chart.getXAxis().setTextSize(13f);
        chart.getAxisLeft().setTextSize(13f);
        chart.getDescription().setEnabled(false); //Убирает подпись на графе с информацией
        chart.setScaleEnabled(false ); //запрещает зум
        chart.setTouchEnabled(true);
        chart.setDragEnabled(false); //запрещает движение
        chart.setDoubleTapToZoomEnabled(false); //запрещает двойной нажатие
        chart.setDrawMarkers(true); //включает отображение маркеров
        chart.setHighlightPerTapEnabled(true);
        chart.getLegend().setEnabled(false); //убирает легенду
        chart.setExtraBottomOffset(10f); //дополнительный отступ снизу
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureDataSetProperties(LineDataSet dataSet, GraphOutputData outputData, int index){
        GraphFunction expression = outputData.getExpressions().get(index);

        if(expression.getType() == GraphFunction.Type.OBJECTIVE){
            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            dataSet.setLineWidth(4f);
            dataSet.enableDashedLine(50F, 20F, 0F); //пунктир
            dataSet.setDrawValues(false);
            dataSet.setDrawCircles(true);
            dataSet.setDrawCircleHole(true);
            dataSet.setCircleColor(Color.GRAY);
            dataSet.setCircleHoleColor(Color.RED);
            dataSet.setCircleRadius(3); //радиус точки
            dataSet.setCircleHoleRadius(2); //радиус обводки
            dataSet.setHighLightColor(Color.TRANSPARENT);
            dataSet.setColor(Color.BLACK);
            return;
        }

        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setLineWidth(5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(true);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleColor(Color.GRAY);
        dataSet.setCircleHoleColor(Color.BLACK);
        dataSet.setCircleRadius(3);
        dataSet.setCircleHoleRadius(2);
        dataSet.setHighLightColor(Color.TRANSPARENT);
        dataSet.setDrawFilled(true); //разрешает закрашивание под/над графиком

        if(expression.getType() == GraphFunction.Type.ARTIFICIAL && currentColor != -1)
            --currentColor;

        int color = choseColor();
        dataSet.setColor(color);
        dataSet.setFillColor(color);

        if(expression.getSign() == Constants.Sign.EQUALS) {
            dataSet.setDrawFilled(false);
            return;
        }

        dataSet.setFillFormatter((set, dataProvider) -> { //свойста заполнения
            if(expression.getSign() == Constants.Sign.MORE)
                return outputData.getTopBound();
            else if(expression.getSign() == Constants.Sign.LESS)
                return outputData.getBottomBound();
            return 0;
        });
    }

    private void configureViewPort(LineChart chart, GraphOutputData outputData){
        float xMax = (Math.abs(outputData.getRightBound()) - Constants.GRAPH_X_BOUNDS_OFFSET / 2) +
                (Math.abs(outputData.getLeftBound()) - Constants.GRAPH_X_BOUNDS_OFFSET / 2);

        float yMax = (Math.abs(outputData.getTopBound()) - Constants.GRAPH_Y_BOUNDS_OFFSET / 2) +
                (Math.abs(outputData.getBottomBound()) - Constants.GRAPH_Y_BOUNDS_OFFSET / 2);

        float moveTo = outputData.getLeftBound() + Constants.GRAPH_X_BOUNDS_OFFSET / 2;

        chart.setVisibleXRangeMaximum(xMax);
        chart.setVisibleYRangeMaximum(yMax, YAxis.AxisDependency.LEFT);
        chart.moveViewTo(moveTo, (outputData.getTopBound() + outputData.getBottomBound()) / 2,
                YAxis.AxisDependency.LEFT);
    }

    private void showDialog(String error_text){
        AlertDialog dialog = new AlertDialog.Builder(this.getContext()).create();
        dialog.setTitle("Info");
        dialog.setMessage(error_text);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialogInterface, i) -> dialog.dismiss());
        dialog.show();
    }

    private Constants.COLORS nextColor(){
        if(currentColor == Constants.COLORS.values().length - 1){
            currentColor = 0;
            return Constants.COLORS.values()[currentColor];
        }
        currentColor++;
        return Constants.COLORS.values()[currentColor];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int choseColor(){
        Constants.COLORS color = nextColor();

        switch(color){
            case RED:
                return Color.parseColor("#4DFF0000");
            case BLUE:
                return Color.parseColor("#4D02198B");
            case CYAN:
                return Color.parseColor("#4D00FFFF");
            case GREY:
                return Color.parseColor("#4D808080");
            case TEAL:
                return Color.parseColor("#4D008080");
            case GREEN:
                return Color.parseColor("#4D00FF00");
            case ORANGE:
                return Color.parseColor("#4DFFA500");
            case PURPLE:
                return Color.parseColor("#4DA020F0");
            case VIOLET:
                return Color.parseColor("#4D7F00FF");
            case YELLOW:
                return Color.parseColor("#4DFFFF00");
        }
        return Color.parseColor("#4DFF0000");
    }

    private void reportError(GraphOutputData.ErrorType error) {

        switch(error){
            case UNKOWN:
                showDialog("Неизвестная ошибка!");
                break;
            case UNLIMITED:
                showDialog("Бесконечное множество решений!");
                break;
            case NOSOLUTION:
                showDialog("Решение отсутствует!");
                break;
            default:
                break;
        }

    }

    private void setColorToDefault() {
        currentColor = -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.setGraphOutputDataMutableToNull();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_graph_result, container, false);
        return root;
    }
}