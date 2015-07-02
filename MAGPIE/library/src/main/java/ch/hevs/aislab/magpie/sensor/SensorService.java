package ch.hevs.aislab.magpie.sensor;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.android.MagpieService;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;


public class SensorService extends Service {

    private final String TAG = getClass().getName();

    /**
     * String constant used to extract the Messenger "extra" from an intent
     */
    static final String REPLY_MESSENGER = "messenger";

    static final String SENSOR_HANDLER_CLASS = "sensorHandler";

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

    // Used for two way communications with MagpieService
    private Messenger requestMessenger;
    private Messenger replyMessenger;


    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Class<? extends SensorHandler> sensorHandlerClass =
                (Class<? extends SensorHandler>) intent.getSerializableExtra(SENSOR_HANDLER_CLASS);

        // Create and start a background HandlerThread since by default a Service
        // runs in the UI Thread, which we don't want to block
        HandlerThread thread = new HandlerThread(sensorHandlerClass.getSimpleName());
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        try {
            mSensorHandler = sensorHandlerClass.getConstructor(Looper.class).newInstance(mServiceLooper);
            mSensorHandler.setSensorService(this);
        } catch (InstantiationException e) {
            Log.e(TAG, "InstantiationException");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "InvocationTargetException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "NoSuchMethodException");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException");
        }

        // Start the connection with the sensor
        Message message = SensorHandler.makeStartConnectionMessage(intent);
        mSensorHandler.sendMessage(message);

        // Assign the messenger to process alerts from the Environment
        replyMessenger = new Messenger(new ReplyHandler(mSensorHandler));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "SensorService - onDestroy()");
        Message message = SensorHandler.makeStopConnectionMessage(mServiceLooper);
        mSensorHandler.sendMessage(message);
        unbindService(twoWayConnection);
    }

    void bindToMagpieService() {
        Intent intentTwoWays = MagpieService.makeIntent(this);
        intentTwoWays.setAction(MagpieActivity.ACTION_TWO_WAY_COMM);
        bindService(intentTwoWays, twoWayConnection, Context.BIND_AUTO_CREATE);
    }

    void sendEvent(MagpieEvent event) {
        Message request = Message.obtain();
        request.replyTo = replyMessenger;

        Bundle bundle = new Bundle();
        bundle.putParcelable(MagpieActivity.MAGPIE_EVENT, event);

        request.setData(bundle);

        try {
            requestMessenger.send(request);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in SensorService");
        }
    }

    public static Intent makeIntent(
            Context context,
            Class<? extends SensorHandler> sensorHandlerImpl,
            Handler sensorHandler) {
        Intent intent = new Intent(context, SensorService.class);
        intent.setAction(START_SENSOR);
        intent.putExtra(REPLY_MESSENGER, new Messenger(sensorHandler));
        intent.putExtra(SENSOR_HANDLER_CLASS, sensorHandlerImpl);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection twoWayConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            requestMessenger = new Messenger(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * Handler to process the alerts coming from the Environment
     */
    private static class ReplyHandler extends Handler {

        private WeakReference<SensorHandler> mSensorHandler;

        public ReplyHandler(SensorHandler sensorHandler) {
            mSensorHandler = new WeakReference<>(sensorHandler);
        }

        @Override
        public void handleMessage(Message reply) {
            SensorHandler sensorHandler = mSensorHandler.get();
            if (sensorHandler == null) {
                return;
            }

            Bundle bundleAlert = reply.getData();
            LogicTupleEvent alert = bundleAlert.getParcelable(MagpieActivity.MAGPIE_EVENT);
            sensorHandler.onAlertProduced(alert);
        }
    }
}