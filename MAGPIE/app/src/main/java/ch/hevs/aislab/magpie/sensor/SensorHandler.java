package ch.hevs.aislab.magpie.sensor;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.android.MagpieService;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public abstract class SensorHandler extends Handler implements SensorConnection {

    private final String TAG = getClass().getName();

    // Used for two way communications with the service
    private Messenger requestMessenger;
    private Messenger replyMessenger = new Messenger(new ReplyHandler(this));

    protected SensorService mSensorService;

    private static final int START_CONNECTION = 100;
    private static final int STOP_CONNECTION = 200;


    public SensorHandler(Looper looper, SensorService sensorService) {
        super(looper);
        mSensorService = sensorService;
    }

    public Message makeStartConnectionMessage(Intent intent, int startId) {
        Message message = Message.obtain();
        message.obj = intent;
        message.arg1 = START_CONNECTION;
        return message;
    }

    public Message makeStopConnectionMessage(Looper sensorServiceLooper) {
        Message message = Message.obtain();
        message.obj = sensorServiceLooper;
        message.arg1 = STOP_CONNECTION;
        return message;
    }

    /**
     * Tries to communicate with Sensor to start or stop a connection
     */
    public void handleMessage(Message message) {
        int type = message.arg1;
        if (type == START_CONNECTION) {
            connectToSensorAndReply((Intent) message.obj);
        } else if (type == STOP_CONNECTION) {
            stopSensorConnection((Looper) message.obj);
        } else {
            MagpieEvent ev = processSensorMessage(message);
            sendEvent(ev);
        }
    }

    private void connectToSensorAndReply(Intent intent) {
        // Extract the Messenger from the Intent
        Messenger messenger = (Messenger) intent.getExtras().get(SensorService.MESSENGER);
        // Get the replyCode to be returned back to the MagpieActivity
        int replyCode = onStartConnection();

        Message message = Message.obtain();
        message.arg1 = replyCode;
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopSensorConnection(Looper sensorServiceLooper) {
        onStopConnection();
        sensorServiceLooper.quit();
    }

    protected void connectToAgentEnvironment() {
        Intent i = MagpieService.makeIntent(mSensorService);
        i.setAction(MagpieActivity.ACTION_TWO_WAY_COMM);
        mSensorService.bindService(i, twoWayConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendEvent(MagpieEvent event) {
        Message request = Message.obtain();
        request.replyTo = replyMessenger;

        Bundle bundle = new Bundle();
        bundle.putParcelable(MagpieActivity.MAGPIE_EVENT, event);

        request.setData(bundle);

        try {
            requestMessenger.send(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
