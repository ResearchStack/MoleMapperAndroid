package org.researchstack.molemapper.ui;
import android.content.res.Resources;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.researchstack.molemapper.R;

import java.util.List;

/**
 * Created by bradleymcdermott on 2/23/16.
 */
public class TempGraphHelper
{
    public static LineChart updateLineChart(LineChart chart, int max, List<Entry> entries, List<String> xValues)
    {
        Resources res = chart.getContext().getResources();

        chart.setDrawBorders(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setYOffset(32f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(14);
        xAxis.setTextColor(res.getColor(R.color.mm_warm_grey));
        xAxis.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawZeroLine(false);
        yAxis.setAxisMaxValue(max + 2);
        yAxis.setAxisMinValue(0);
        yAxis.setShowOnlyMinMax(true);
        yAxis.setTextSize(14);
        yAxis.setTextColor(res.getColor(R.color.mm_warm_grey));
        yAxis.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");

        LineDataSet set = new LineDataSet(entries, "");
        set.setCircleColor(res.getColor(R.color.mm_colorPrimary));
        set.setCircleRadius(4f);
        set.setDrawCircleHole(false);
        set.setColor(res.getColor(R.color.mm_colorPrimary));
        set.setLineWidth(2f);
        set.setDrawValues(false);

        LineData data = new LineData(xValues, set);
        chart.setData(data);
        chart.setVisibleXRange(0, 7);

        return chart;
    }

}
