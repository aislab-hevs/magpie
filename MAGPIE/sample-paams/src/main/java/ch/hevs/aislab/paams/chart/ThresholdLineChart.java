package ch.hevs.aislab.paams.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class ThresholdLineChart extends LineChart {

    protected Paint mYAxisSafeZonePaint;

    public ThresholdLineChart(Context context) {
        super(context);
    }

    public ThresholdLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThresholdLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mYAxisSafeZonePaint = new Paint();
        mYAxisSafeZonePaint.setStyle(Paint.Style.FILL);
        mGridBackgroundPaint.setColor(Color.rgb(204, 255, 153));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        List<LimitLine> limitLines = mAxisLeft.getLimitLines();

        /*
        Workaround for a known crash if working with empty datasets
        which can happen due to dummy data enabling/disabling.
        https://github.com/PhilJay/MPAndroidChart/pull/2462/commits
        */
        if (isEmpty()) {
            MPPointF c = getCenter();
            canvas.drawText("No chart data.", c.x, c.y, mInfoPaint);
            return;
        }

        // Weight:
        if (limitLines == null) {
            super.onDraw(canvas);
            return;
        }

        // Glucose:
        if (limitLines.size() == 6) {
            LimitLine l0 = limitLines.get(0);
            LimitLine l1 = limitLines.get(1);
            LimitLine l2 = limitLines.get(2);
            LimitLine l3 = limitLines.get(3);
            LimitLine l4 = limitLines.get(4);
            LimitLine l5 = limitLines.get(5);

            float[] pts1 = new float[4];
            pts1[1] = l0.getLimit();
            pts1[3] = l1.getLimit();

            float[] pts2 = new float[4];
            pts2[1] = l2.getLimit();
            pts2[3] = l3.getLimit();

            float[] pts3 = new float[4];
            pts3[1] = l4.getLimit();
            pts3[3] = l5.getLimit();

            l0.setEnabled(false);
            l1.setEnabled(false);
            l2.setEnabled(false);
            l3.setEnabled(false);
            l4.setEnabled(false);
            l5.setEnabled(false);

            mLeftAxisTransformer.pointValuesToPixel(pts1);
            mLeftAxisTransformer.pointValuesToPixel(pts2);
            mLeftAxisTransformer.pointValuesToPixel(pts3);

            // Glucose: Over threshold
            mYAxisSafeZonePaint.setColor(Color.rgb(255, 204, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts1[1], mViewPortHandler.contentRight(), pts1[3], mYAxisSafeZonePaint);

            // Glucose: In threshold
            mYAxisSafeZonePaint.setColor(Color.rgb(204, 255, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts2[1], mViewPortHandler.contentRight(), pts2[3], mYAxisSafeZonePaint);

            // Glucose: Under threshold
            mYAxisSafeZonePaint.setColor(Color.rgb(255, 204, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts3[1], mViewPortHandler.contentRight(), pts3[3], mYAxisSafeZonePaint);
        }

        // Blood pressure:
        if (limitLines.size() == 10) {
            LimitLine l0 = limitLines.get(0);
            LimitLine l1 = limitLines.get(1);
            LimitLine l2 = limitLines.get(2);
            LimitLine l3 = limitLines.get(3);
            LimitLine l4 = limitLines.get(4);
            LimitLine l5 = limitLines.get(5);
            LimitLine l6 = limitLines.get(6);
            LimitLine l7 = limitLines.get(7);
            LimitLine l8 = limitLines.get(8);
            LimitLine l9 = limitLines.get(9);

            float[] pts1 = new float[4];
            pts1[1] = l0.getLimit();
            pts1[3] = l1.getLimit();

            float[] pts2 = new float[4];
            pts2[1] = l2.getLimit();
            pts2[3] = l3.getLimit();

            float[] pts3 = new float[4];
            pts3[1] = l4.getLimit();
            pts3[3] = l5.getLimit();

            float[] pts4 = new float[4];
            pts4[1] = l6.getLimit();
            pts4[3] = l7.getLimit();

            float[] pts5 = new float[4];
            pts5[1] = l8.getLimit();
            pts5[3] = l9.getLimit();

            l0.setEnabled(false);
            l1.setEnabled(false);
            l2.setEnabled(false);
            l3.setEnabled(false);
            l4.setEnabled(false);
            l5.setEnabled(false);
            l6.setEnabled(false);
            l7.setEnabled(false);
            l8.setEnabled(false);
            l9.setEnabled(false);

            mLeftAxisTransformer.pointValuesToPixel(pts1);
            mLeftAxisTransformer.pointValuesToPixel(pts2);
            mLeftAxisTransformer.pointValuesToPixel(pts3);
            mLeftAxisTransformer.pointValuesToPixel(pts4);
            mLeftAxisTransformer.pointValuesToPixel(pts5);

            // Systolic: Over threshold
            setSafeZoneColor(Color.rgb(255, 204, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts1[1], mViewPortHandler.contentRight(), pts1[3], mYAxisSafeZonePaint);

            // Systolic: In threshold
            setSafeZoneColor(Color.rgb(204, 255, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts2[1], mViewPortHandler.contentRight(), pts2[3], mYAxisSafeZonePaint);

            // Systolic: Under threshold & Diastolic: Over threshold
            setSafeZoneColor(Color.rgb(255, 204, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts3[1], mViewPortHandler.contentRight(), pts3[3], mYAxisSafeZonePaint);

            // Diastolic: In threshold
            setSafeZoneColor(Color.rgb(204, 255, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts4[1], mViewPortHandler.contentRight(), pts4[3], mYAxisSafeZonePaint);

            // Diastolic: Under threshold
            setSafeZoneColor(Color.rgb(255, 204, 204));
            canvas.drawRect(mViewPortHandler.contentLeft(), pts5[1], mViewPortHandler.contentRight(), pts5[3], mYAxisSafeZonePaint);
        }
        super.onDraw(canvas);
    }

    public void setSafeZoneColor(int color) {
        mYAxisSafeZonePaint.setColor(color);
    }
}
