package com.qzc.mobiledlsearch.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.qzc.mobiledlsearch.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class BarChartFragment extends Fragment{

    private static final String EXTRA_TEXT = "text";

    public static BarChartFragment createFor(String text) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private BarChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bar, container, false);

        chart = v.findViewById(R.id.chart1);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be drawn
        chart.setMaxVisibleValueCount(60);
        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);
        setData();

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTextColor(Color.WHITE);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setSpaceTop(35f);

        chart.getAxisRight().setEnabled(false);

        return v;
    }

    private void setData() {

        float groupSpace = 0.02f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.11f; // x4 DataSet
        // (0.11 + 0.03) * 4 + 0.02 = 1.00 -> interval per "group"

        int groupCount = 1;

        BarDataSet set1, set2, set3, set4, set5, set6, set7;

        set1 = new BarDataSet(loadEntriesFromFile("Biking"), "Biking");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(loadEntriesFromFile("Downstairs"), "Downstairs");
        set2.setColor(Color.BLUE);
        set3 = new BarDataSet(loadEntriesFromFile("Jogging"), "Jogging");
        set3.setColor(Color.GREEN);
        set4 = new BarDataSet(loadEntriesFromFile("Sitting"), "Sitting");
        set4.setColor(Color.YELLOW);
        set5 = new BarDataSet(loadEntriesFromFile("Standing"), "Standing");
        set5.setColor(Color.MAGENTA);
        set6 = new BarDataSet(loadEntriesFromFile("Upstairs"), "Upstairs");
        set6.setColor(Color.CYAN);
        set7 = new BarDataSet(loadEntriesFromFile("Walking"), "Walking");
        set7.setColor(Color.LTGRAY);

        BarData data = new BarData(set1, set2, set3, set4, set5, set6, set7);
        data.setValueFormatter(new LargeValueFormatter());
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);

        // specify the width each bar should have
        chart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart.getXAxis().setAxisMinimum(0);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart.groupBars(0, groupSpace, barSpace);
        chart.invalidate();

        chart.getXAxis().setEnabled(false);
    }

    public ArrayList<BarEntry> loadEntriesFromFile(String label) {
        ArrayList<BarEntry> values = new ArrayList<>();
        try {
            InputStream inputStream = this.getContext().openFileInput(label + "-Summary.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                float sum = 0f;
                int number = 0;
                while ( (line = bufferedReader.readLine()) != null ) {
                    String[] split = line.split("#");
                    sum += Float.parseFloat(split[1]);
                    number ++;
                }
                values.add(new BarEntry(0, sum/number));
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("File Read", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Read", "Can not read file: " + e.toString());
        }
        return values;
    }
}
