package ch.hevs.aislab.magpie.android;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import ch.hevs.aislab.magpie.sensor.SensorHandler;
import ch.hevs.aislab.magpie.sensor.SensorService;

public abstract class MagpieActivityBH extends MagpieActivity {

    private Intent sensorServiceIntent;

    // Sensor related fields
    private SensorReplyHandler sensorReplyHandler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorServiceIntent != null) {
            stopService(sensorServiceIntent);
        }
    }

    protected void disconnectSensor() {
        sensorReplyHandler = null;
        stopService(sensorServiceIntent);
    }

    /**
     * Sensor related methods and classes
     */
    protected void connectToSensor(Class<? extends SensorHandler> sensorHandlerImpl) {
        // Initialize the SensorHandler
        sensorReplyHandler = new SensorReplyHandler(this);
        sensorServiceIntent = SensorService.makeIntent(this, sensorHandlerImpl, sensorReplyHandler);
        startService(sensorServiceIntent);
    }

    protected abstract void sensorConnectionResult(int code);

    /**
     * A nested class that uses the handleMessage() hook method to process Messages
     * sent to it from the SensorService
     */
    private static class SensorReplyHandler extends Handler {

        private WeakReference<MagpieActivityBH> mActivity;

        SensorReplyHandler(MagpieActivityBH activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message message) {
            int result = message.arg1;
            MagpieActivityBH activity = mActivity.get();
            if (activity == null) {
                return;
            }
            activity.sensorConnectionResult(result);
        }
    }

}
