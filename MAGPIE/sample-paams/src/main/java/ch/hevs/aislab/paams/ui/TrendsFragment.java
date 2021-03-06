package ch.hevs.aislab.paams.ui;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.chart.ThresholdLineChart;
import ch.hevs.aislab.paams.chart.ValueMarkerView;
import ch.hevs.aislab.paams.chart.XAxisFormatter;
import ch.hevs.aislab.paams.connector.AlertDAO;
import ch.hevs.aislab.paams.chart.ValueChartAdapter;
import ch.hevs.aislab.paams.connector.ValueDAO;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class TrendsFragment extends Fragment implements MainActivity.OnChangeDummyDataDisplayListener{

    private static final String TAG = "TrendsFragment";

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";
    private static final String BUNDLE_KEY = "items";

    private Type type;
    private ValueChartAdapter valueChartAdapter;
    private static LineChart chart;
    private ValueDAO valueDAO;
    private AlertDAO alertDAO;
    private Boolean displayDummyData;

    public TrendsFragment() {

    }

    public static TrendsFragment newInstance(Type type) {
        TrendsFragment fragment = new TrendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach()");
        displayDummyData = ((MainActivity) getActivity()).setOnChangeDummyDataDisplayListener(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate() for " + type);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }


        if (savedInstanceState == null) {
            valueChartAdapter = new ValueChartAdapter(type);
        } else {
            if (savedInstanceState.getParcelableArray(BUNDLE_KEY) != null) {
                Value[] values = (Value[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
                List<Value> valuesList = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    valuesList.add(values[i]);
                }
                valueChartAdapter = new ValueChartAdapter(valuesList, getContext());
            }
        }

        valueDAO = new ValueDAO(getActivity());
        valueDAO.open();
        if (valueChartAdapter.getCount() == 0) {
            valueChartAdapter.addAllItems(valueDAO.getAllValues(type), getContext());
        }
        alertDAO = new AlertDAO(getActivity());
        alertDAO.open();
        valueChartAdapter.addAllAlerts(alertDAO.getAlertsByType(type), getContext());
        if (chart != null)
            chart.invalidate();
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart() from " + type);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume() from " + type);
        if (chart != null)
            chart.invalidate();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause() from " + type);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop() from " + type);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView() for " + type);
        View view = inflater.inflate(R.layout.fragment_trends, container, false);
        setupChart(view);
        setupLegend();
        valueChartAdapter.displayDummyData(displayDummyData);
        chart.notifyDataSetChanged();
        chart.invalidate();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy() for " + type);
        valueDAO.close();
        alertDAO.close();
        super.onDestroy();
    }

    @Override
    public void displayDummyData(Boolean display) {
        Log.i(TAG, "displayDummyData() for type " + type + " with " + display);
        valueChartAdapter.displayDummyData(display);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void setupChart(View view) {
        chart = (ThresholdLineChart) view.findViewById(R.id.chart);
        YAxis yAxisLeft = chart.getAxisLeft();
        YAxis yAxisRight = chart.getAxisRight();
        XAxis xAxis = chart.getXAxis();
        yAxisLeft.setTextSize(12f);
        yAxisRight.setTextSize(12f);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setData(valueChartAdapter.getLineData());
        chart.setExtraTopOffset(5f);
        LimitLine l0;
        LimitLine l1;
        switch (type) {
            case GLUCOSE:
                // Glucose: Over threshold
                l0 = new LimitLine(8.0f);
                setupLimitLine(l0);
                yAxisLeft.addLimitLine(l0);
                // Glucose: Under threshold
                l1 = new LimitLine(3.8f);
                setupLimitLine(l1);
                yAxisLeft.addLimitLine(l1);
                break;
            case BLOOD_PRESSURE:
                // Systolic: Over threshold
                yAxisLeft.addLimitLine(new LimitLine(139f));
                yAxisLeft.addLimitLine(new LimitLine(149f));
                // Systolic: In threshold
                yAxisLeft.addLimitLine(new LimitLine(139f));
                yAxisLeft.addLimitLine(new LimitLine(120f));
                // Systolic: Under threshold
                yAxisLeft.addLimitLine(new LimitLine(120f));
                yAxisLeft.addLimitLine(new LimitLine(89f));
                // Diastolic: In threshold
                yAxisLeft.addLimitLine(new LimitLine(89f));
                yAxisLeft.addLimitLine(new LimitLine(80f));
                // Diastolic: Under threshold
                yAxisLeft.addLimitLine(new LimitLine(80f));
                yAxisLeft.addLimitLine(new LimitLine(53f));
                break;
            case WEIGHT:
                // Weight: Over threshold
                l0 = new LimitLine(94.6f);
                setupLimitLine(l0);
                yAxisLeft.addLimitLine(l0);
                // Weight: In threshold
                l1 = new LimitLine(93.7f);
                setupLimitLine(l1);
                yAxisLeft.addLimitLine(l1);
                break;
        }
        xAxis.setAxisMaximum(xAxis.getAxisMaximum() + 300f);
        xAxis.setAxisMinimum(xAxis.getAxisMinimum() - 300f);
        XAxisFormatter formatter = new XAxisFormatter();
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextSize(12f);
        xAxis.setDrawLimitLinesBehindData(true);
        yAxisLeft.setDrawLimitLinesBehindData(true);
        yAxisRight.setDrawLimitLinesBehindData(true);
        ValueMarkerView markerView = new ValueMarkerView(getContext(), R.layout.fragment_trends);
        chart.setMarker(markerView);
        chart.invalidate();
    }

    private void setupLimitLine(LimitLine limitLine) {
        limitLine.setLineWidth(2);
        limitLine.setLineColor(Color.rgb(76, 153, 0));
        limitLine.enableDashedLine(15f, 5f, 0f);
    }

    private void setupLegend() {
        Legend legend = chart.getLegend();
        List<LegendEntry> legendEntries = new ArrayList<>();
        if (legend.getEntries().length == 0)
            return;
        legendEntries.add(new LegendEntry(legend.getEntries()[0].label, Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, ContextCompat.getColor(getContext(), R.color.set1)));
        if (type == Type.BLOOD_PRESSURE)
            legendEntries.add(new LegendEntry(legend.getEntries()[1].label, Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, ContextCompat.getColor(getContext(), R.color.set2)));
        legendEntries.add(new LegendEntry("Alert", Legend.LegendForm.DEFAULT, Float.NaN, Float.NaN, null, ContextCompat.getColor(getContext(), R.color.setAlert)));
        legend.setCustom(legendEntries);
        legend.setTextSize(15f);
        chart.invalidate();
    }
}
