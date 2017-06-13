package ch.hevs.aislab.paams.chart;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class ValueMarkerView extends MarkerView {

    private TextView tvContent;
    private DateFormat dateFormat;
    private Date date;

    public ValueMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
        LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setNoDataText("");
        this.dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        this.date = new Date();
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Log.i("", e.getData().getClass() + "");
        if (((Value) e.getData()).getType() == Type.GLUCOSE)
            tvContent.setText(" " + getTime(((Value) e.getData()).getTimestamp()) + " \n Glucose: " + ((SingleValue) e.getData()).getValue() + " mmol/L ");
        if (((Value) e.getData()).getType() == Type.WEIGHT)
            tvContent.setText(" " + getTime(((Value) e.getData()).getTimestamp()) + " \n Weight: " + ((SingleValue) e.getData()).getValue() + " kg ");
        if (((Value) e.getData()).getType() == Type.BLOOD_PRESSURE)
            tvContent.setText(" " + getTime(((Value) e.getData()).getTimestamp()) + " \n Systolic: " + ((DoubleValue) e.getData()).getFirstValue() + " mmHg \n Diastolic:" + ((DoubleValue) e.getData()).getSecondValue() + " mmHg ");
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {
        if (mOffset == null) {
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }
        return mOffset;
    }

    private String getTime(long timestamp) {
        date.setTime(timestamp);
        return dateFormat.format(date);
    }
}
