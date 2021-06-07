package com.qzc.mobiledlsearch.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.utils.MyMarkerView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ReportFragment extends Fragment{

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    private ImageView btn_back;

    public static ReportFragment createFor(String text) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private LineChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line, container, false);

        chart = v.findViewById(R.id.lineChart1);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        LineData data = getComplexity();
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(0f);

        chart.setData(data);
        chart.animateX(3000);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);

        return v;
    }

    protected LineData getComplexity() {

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        LineDataSet ds1 = new LineDataSet(loadEntriesFromFile("Biking"), "Biking");
        LineDataSet ds2 = new LineDataSet(loadEntriesFromFile("Downstairs"), "Downstairs");
        LineDataSet ds3 = new LineDataSet(loadEntriesFromFile("Jogging"), "Jogging");
        LineDataSet ds4 = new LineDataSet(loadEntriesFromFile("Sitting"), "Sitting");
        LineDataSet ds5 = new LineDataSet(loadEntriesFromFile("Standing"), "Standing");
        LineDataSet ds6 = new LineDataSet(loadEntriesFromFile("Upstairs"), "Upstairs");
        LineDataSet ds7 = new LineDataSet(loadEntriesFromFile("Walking"), "Walking");

        ds1.setColor(Color.RED);
        ds2.setColor(Color.BLUE);
        ds3.setColor(Color.GREEN);
        ds4.setColor(Color.YELLOW);
        ds5.setColor(Color.MAGENTA);
        ds6.setColor(Color.CYAN);
        ds7.setColor(Color.LTGRAY);

        ds1.setCircleColor(Color.RED);
        ds2.setCircleColor(Color.BLUE);
        ds3.setCircleColor(Color.GREEN);
        ds4.setCircleColor(Color.YELLOW);
        ds5.setCircleColor(Color.MAGENTA);
        ds6.setCircleColor(Color.CYAN);
        ds7.setCircleColor(Color.LTGRAY);

        ds1.setLineWidth(2.5f);
        ds1.setCircleRadius(3f);
        ds2.setLineWidth(2.5f);
        ds2.setCircleRadius(3f);
        ds3.setLineWidth(2.5f);
        ds3.setCircleRadius(3f);
        ds4.setLineWidth(2.5f);
        ds4.setCircleRadius(3f);
        ds5.setLineWidth(2.5f);
        ds5.setCircleRadius(3f);
        ds6.setLineWidth(2.5f);
        ds6.setCircleRadius(3f);
        ds7.setLineWidth(2.5f);
        ds7.setCircleRadius(3f);

        // load DataSets from files in assets folder
        sets.add(ds1);
        sets.add(ds2);
        sets.add(ds3);
        sets.add(ds4);
        sets.add(ds5);
        sets.add(ds6);
        sets.add(ds7);

        LineData d = new LineData(sets);
        return d;
    }

    public List<Entry> loadEntriesFromFile(String label) {

        List<Entry> entries = new ArrayList<Entry>();
        try {
            InputStream inputStream = this.getContext().openFileInput(label + ".txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ( (line = bufferedReader.readLine()) != null ) {
                    String[] split = line.split("#");
                    entries.add(new Entry(Integer.parseInt(split[0]), Float.parseFloat(split[1])));

                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("File Read", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Read", "Can not read file: " + e.toString());
        }
        return entries;
    }
}
