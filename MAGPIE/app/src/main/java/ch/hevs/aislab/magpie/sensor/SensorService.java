package ch.hevs.aislab.magpie.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;


public abstract class SensorService extends Service {

    private final String TAG = getClass().getName();

    /**
     * String constant used to extract the Messenger "extra" from an intent
     */
    static final String MESSENGER = "messenger";

    /**
     * Possible actions in the Intent that starts the SensorService
     */
    public static final String START_SENSOR = "startSensor";

    /**
     * Looper associated with the HandlerThread
     */
    private volatile Looper mServiceLooper;

    /**
     * Process Messages sent to it from onStartCommand()
     */
    private volatile SensorHandler mSensorHandler;


    public void onCreateSensorService(SensorService sensorService,
                                      String threadName,
                                      Class<? extends SensorHandler> sensorHandler) {
        // Create and start a background HandlerThread since by default a Service
        // runs in the UI Thread, which we don't want to block
        HandlerThread thread = new HandlerThread(threadName);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();

        try {
            mSensorHandler = sensorHandler
                    .getConstructor(Looper.class, SensorService.class)
                    .newInstance(mServiceLooper, sensorService);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void onStartSensorServiceCommand(
            Intent intent,
            int flags,
            int startId) {
        // Start the connection with the sensor
        Message message = mSensorHandler.makeStartConnectionMessage(intent, startId);
        mSensorHandler.sendMessage(message);
    }

    public void onDestroySensorService() {
        Log.i(TAG, "onDestroySensorService()");
        Message message = mSensorHandler.makeStopConnectionMessage(mServiceLooper);
        mSensorHandler.sendMessage(message);
    }

    public static Intent makeIntent(
            Context context,
            Class<? extends SensorService> sensorServiceImpl,
            Handler sensorHandler) {
        Intent intent = new Intent(context, sensorServiceImpl);
        intent.setAction(START_SENSOR);
        intent.putExtra(MESSENGER, new Messenger(sensorHandler));
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}