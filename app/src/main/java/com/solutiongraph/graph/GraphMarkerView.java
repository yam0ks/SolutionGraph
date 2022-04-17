package com.solutiongraph.graph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.solutiongraph.R;

@SuppressLint("ViewConstructor")
public class GraphMarkerView extends MarkerView { //Класс, регулирующий отображение значений при
                                                  // нажатии на точки графика

    private final TextView tvContent;

    public GraphMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry entry, Highlight highlight) {

        if (entry instanceof CandleEntry) {

            CandleEntry candleEntry = (CandleEntry) entry;

            tvContent.setText(Utils.formatNumber(candleEntry.getHigh(), 1, true));
        } else {

            tvContent.setText("(" + Utils.formatNumber(entry.getX(), 2, true)
                    + "; " + Utils.formatNumber(entry.getY(), 2, true) + ")");
        }

        super.refreshContent(entry, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

