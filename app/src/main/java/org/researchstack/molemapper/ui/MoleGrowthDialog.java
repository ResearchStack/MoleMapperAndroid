package org.researchstack.molemapper.ui;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.molemapper.MoleHistoryActivity;
import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Measurement;
import org.researchstack.molemapper.models.Mole;
import org.researchstack.molemapper.models.MoleDetail;
import org.researchstack.molemapper.ui.view.MoleDetailView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MoleGrowthDialog
{
    private static final String           FORMAT_MONTH        = "MMM";
    private static final String           FORMAT_YEAR_MONTH   = "yyyyMM";
    private static       SimpleDateFormat DATE_FORMAT_KEY     = new SimpleDateFormat(
            FORMAT_YEAR_MONTH);
    private static       SimpleDateFormat DATE_FORMAT_DISPLAY = new SimpleDateFormat(FORMAT_MONTH);

    public static Dialog showGrowthDialog(Context context, Mole mole, MoleGrowthCallbacks callbacks)
    {
        MoleDetail detail = new MoleDetail(mole);

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.dialog_mole, null);

        Dialog dialog = new AlertDialog.Builder(context, R.style.Dialog_Body).show();
        dialog.setCancelable(true);

        // Set header information
        MoleDetailView moleDetailView = (MoleDetailView) layout.findViewById(R.id.dialog_mole_info_container);
        moleDetailView.setModel(detail, false);

        View graphContainer = layout.findViewById(R.id.dialog_mole_graph_container);
        if(mole.measurements.size() > 1)
        {
            initChart(context, dialog, graphContainer, mole);
        }
        else
        {
            graphContainer.setVisibility(View.GONE);
        }

        // Edit mole
        layout.findViewById(R.id.dialog_mole_edit).setOnClickListener(v -> {
            callbacks.onEditMole();
            dialog.dismiss();
        });

        // Cancel dialog
        layout.findViewById(R.id.dialog_mole_cancel).setOnClickListener(v -> dialog.dismiss());

        // Update Mole
        layout.findViewById(R.id.dialog_mole_update).setOnClickListener(v -> {

            callbacks.onUpdateMole();
            dialog.dismiss();
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.setContentView(layout);
        dialog.getWindow().setAttributes(params);

        return dialog;
    }

    private static void initChart(Context context, Dialog dialog, View view, Mole mole)
    {
        TextView title = (TextView) view.findViewById(R.id.chart_title);
        title.setText(R.string.mole_size_over_time);
        LineChart chart = (LineChart) view.findViewById(R.id.line_chart);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);

        RxView.clicks(view.findViewById(R.id.chart_history)).subscribe(o -> {
            dialog.dismiss();

            Intent historyIntent = MoleHistoryActivity.newIntent(context, mole);
            context.startActivity(historyIntent);
        });

        // First, we wrap the mole measurements in a new List, as to not modify the original
        // data-structure. Then we sort the measurements by date in ascending order.
        ArrayList<Measurement> measurements = new ArrayList<>(mole.measurements);
        Collections.sort(measurements, (m1, m2) -> m1.date.compareTo(m2.date));

        // We create a HashMap and insert entries based on "yyyyMM". We will end up with a list of
        // the last measurements for each month
        HashMap<String, Measurement> filteredMap = new LinkedHashMap<>();

        // Iterate through measurements, insert into HashMap
        for(Measurement measurement : measurements)
        {
            filteredMap.put(DATE_FORMAT_KEY.format(measurement.date), measurement);
        }

        // Time to generate some labels and out entries list (Used by the graph). Could probably
        // do something with keyFormat and substring to get "MMM" .... "to-do"
        List<Measurement> filteredMeasurements = new ArrayList<>(filteredMap.values());
        List<String> filteredXValues = new ArrayList<>();
        List<Entry> entryList = new ArrayList<>();

        // Add label to beginning of chart. This is hack due to the chart class having no way of
        // adding padding to where the line is drawn.
        filteredXValues.add(0, "");

        // Keep track fo maxMoleDiameter for TempGraphHelper.updateLineChart();
        float maxMoleDiameter = 0;

        // Offset each xPosition by one to compensate for
        for(int i = 0, size = filteredMeasurements.size(); i < size; i++)
        {
            Measurement filtered = filteredMeasurements.get(i);
            entryList.add(new Entry(filtered.absoluteMoleDiameter, i + 1));
            maxMoleDiameter = filtered.absoluteMoleDiameter > maxMoleDiameter
                    ? filtered.absoluteMoleDiameter
                    : maxMoleDiameter;
            filteredXValues.add(DATE_FORMAT_DISPLAY.format(filtered.date));
        }

        // Add label a the ends of the list to add some space to the chart
        filteredXValues.add("");

        // Update chart w/ our data
        TempGraphHelper.updateLineChart(chart, (int) maxMoleDiameter, entryList, filteredXValues);

        // Move to "end" of chart
        chart.moveViewToX(filteredXValues.size());
    }

    public interface MoleGrowthCallbacks
    {
        void onEditMole();

        void onUpdateMole();
    }
}
