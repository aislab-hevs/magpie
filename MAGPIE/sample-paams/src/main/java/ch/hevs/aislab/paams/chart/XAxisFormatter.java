package ch.hevs.aislab.paams.chart;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class XAxisFormatter implements IAxisValueFormatter {

    private static final String TAG = "XAxisFormatter";

    private DateFormat dateFormat;
    private Date date;

    public XAxisFormatter() {
        this.dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        this.date = new Date();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long timestamp = TimeUnit.HOURS.toMillis((long) value);
        return getDate(timestamp);
    }

    private String getDate(long timestamp) {
        date.setTime(timestamp);
        return dateFormat.format(date);
    }
}
