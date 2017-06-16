package ch.hevs.aislab.paams.chart;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.hevs.aislab.paams.model.Alert;
import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class ValueChartAdapter {

    private static final String TAG = "ValueChartAdapter";
    private static final String LABEL_WEIGHT = "Weight [kg]";
    private static final String LABEL_GLUCOSE = "Glucose [mmol/L]";
    private static final String LABEL_SYSTOLIC = "Systolic [mmHg]";
    private static final String LABEL_DIASTOLIC = "Diastolic [mmHg]";

    Type type;
    List<LineDataSet> dataSets;
    List<LineDataSet> dummyDataSets;
    LineData lineData;
    int[] colorsSet1;
    int[] colorsSet2;

    public ValueChartAdapter(Type type) {
        this.type = type;
        if (dummyDataSets == null) {
            dummyDataSets = new ArrayList<>();
        }
    }

    public ValueChartAdapter(List<Value> values, Context context) {
        initializeChartData(values, context);
    }

    public void addAllItems(List<Value> values, Context context) {
        initializeChartData(values, context);
    }

    public void addAllAlerts(List<Alert> alerts, Context context) {
        if (alerts.isEmpty()) {
            return;
        }
        sortItemsByDate(alerts);
        LineDataSet dataSet1;
        LineDataSet dataSet2;
        int index = 0;
        switch (type) {
            case GLUCOSE:
                Arrays.fill(colorsSet1, R.color.set1);
                dataSet1 = (LineDataSet) lineData.getDataSetByLabel(LABEL_GLUCOSE, false);
                for (Alert alert : alerts) {
                    alert.setTimestamp(alert.getTimestamp() - 1);
                    index = dataSet1.getEntryIndex(convertTimestamp(alert.getTimestamp()), 0f, DataSet.Rounding.CLOSEST);
                    colorsSet1[index] = R.color.setAlert;
                }
                dataSet1.setCircleColors(colorsSet1, context);
                dataSet1.setCircleRadius(5f);
                break;
            case WEIGHT:
                Arrays.fill(colorsSet1, R.color.set1);
                dataSet1 = (LineDataSet) lineData.getDataSetByLabel(LABEL_WEIGHT, false);
                for (Alert alert : alerts) {
                    alert.setTimestamp(alert.getTimestamp() - 1);
                    index = dataSet1.getEntryIndex(convertTimestamp(alert.getTimestamp()), 0f, DataSet.Rounding.CLOSEST);
                    colorsSet1[index] = R.color.setAlert;
                }
                dataSet1.setCircleColors(colorsSet1, context);
                dataSet1.setCircleRadius(5f);
                break;
            case BLOOD_PRESSURE:
                Arrays.fill(colorsSet1, R.color.set1);
                Arrays.fill(colorsSet2, R.color.set2);
                dataSet1 = (LineDataSet) lineData.getDataSetByLabel(LABEL_SYSTOLIC, false);
                dataSet2 = (LineDataSet) lineData.getDataSetByLabel(LABEL_DIASTOLIC, false);
                for (Alert alert : alerts) {
                    alert.setTimestamp(alert.getTimestamp() - 1);
                    index = dataSet1.getEntryIndex(convertTimestamp(alert.getTimestamp()), 0f, DataSet.Rounding.CLOSEST);
                    colorsSet1[index] = R.color.setAlert;
                    colorsSet2[index] = R.color.setAlert;
                }
                dataSet1.setCircleColors(colorsSet1, context);
                dataSet1.setCircleRadius(5f);
                dataSet2.setCircleColors(colorsSet2, context);
                dataSet2.setCircleRadius(5f);
                break;
        }
    }

    public int getCount() {
        if (lineData != null) {
            return lineData.getEntryCount();
        }
        return 0;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void displayDummyData(boolean show) {
        List<Entry> dummyEntries1 = new ArrayList<>();
        List<Entry> dummyEntries2 = new ArrayList<>();
        Log.i(TAG, "displayDummyData with " + show);
        if (show) {
            if (!dummyDataSets.isEmpty()) {
                for (Entry entry : (List<Entry>) ((DataSet) dummyDataSets.get(0)).getValues())
                    dataSets.get(0).addEntry(entry);
                if (type == Type.BLOOD_PRESSURE) {
                    for (Entry entry : (List<Entry>) ((DataSet) dummyDataSets.get(1)).getValues())
                        dataSets.get(1).addEntry(entry);
                }
                dummyDataSets.clear();
            }
        } else {
            if (dataSets == null || dataSets.isEmpty())
                return;
            Iterator<Entry> iterator = ((DataSet) dataSets.get(0)).getValues().iterator();
            while (iterator.hasNext()) {
                Entry entry = iterator.next();
                if (((Value) entry.getData()).isDummy()) {
                    dummyEntries1.add(entry);
                    iterator.remove();
                }
            }
            dummyDataSets.add(new LineDataSet(dummyEntries1, dataSets.get(0).getLabel()));
            if (type == Type.BLOOD_PRESSURE) {
                iterator = ((DataSet) dataSets.get(1)).getValues().iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (((Value) entry.getData()).isDummy()) {
                        dummyEntries2.add(entry);
                        iterator.remove();
                    }
                }
                dummyDataSets.add(new LineDataSet(dummyEntries2, dataSets.get(1).getLabel()));
            }
        }
    }

    private void initializeChartData(List<Value> values, Context context) {
        if (values.isEmpty()) {
            return;
        }
        if (dummyDataSets == null) {
            dummyDataSets = new ArrayList<>();
        }
        colorsSet1 = new int[values.size()];
        colorsSet2 = new int[values.size()];
        Arrays.fill(colorsSet1, R.color.set1);
        Arrays.fill(colorsSet2, R.color.set2);
        sortItemsByDate(values);
        dataSets = new ArrayList<>();
        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        switch (type) {
            case GLUCOSE:
                for (Value value : values) {
                    entries1.add(new Entry(convertTimestamp(value.getTimestamp()), (float) (((SingleValue) value).getValue()), value));
                }
                LineDataSet glucoseSet = new LineDataSet(entries1, LABEL_GLUCOSE);
                glucoseSet.setCircleColors(colorsSet1, context);
                glucoseSet.setCircleRadius(5f);
                dataSets.add(glucoseSet);
                break;
            case WEIGHT:
                for (Value value : values) {
                    entries1.add(new Entry(convertTimestamp(value.getTimestamp()), (float) (((SingleValue) value).getValue()), value));
                }
                LineDataSet weightSet = new LineDataSet(entries1, LABEL_WEIGHT);
                weightSet.setCircleColors(colorsSet1, context);
                weightSet.setCircleRadius(5f);
                dataSets.add(weightSet);
                break;
            case BLOOD_PRESSURE:
                for (Value value : values) {
                    entries1.add(new Entry(convertTimestamp(value.getTimestamp()), (float) (((DoubleValue) value).getFirstValue()), value));
                    entries2.add(new Entry(convertTimestamp(value.getTimestamp()), (float) (((DoubleValue) value).getSecondValue()), value));
                }
                LineDataSet systolicSet = new LineDataSet(entries1, LABEL_SYSTOLIC);
                systolicSet.setCircleColors(colorsSet1, context);
                systolicSet.setCircleRadius(5f);
                LineDataSet diastolicSet = new LineDataSet(entries2, LABEL_DIASTOLIC);
                diastolicSet.setCircleColors(colorsSet2, context);
                diastolicSet.setCircleRadius(5f);
                dataSets.add(systolicSet);
                dataSets.add(diastolicSet);
                break;
        }
        lineData = new LineData();
        for (LineDataSet dataSet : dataSets) {
            dataSet.enableDashedLine(0.1f, 10000f, 0);
            dataSet.setDrawValues(false);
            lineData.addDataSet(dataSet);
        }
    }

    private void sortItemsByDate(List<? extends Comparable> values) {
        Collections.sort(values);
    }

    private long convertTimestamp(long timestamp) {
        return TimeUnit.MILLISECONDS.toHours(timestamp);
    }
}
