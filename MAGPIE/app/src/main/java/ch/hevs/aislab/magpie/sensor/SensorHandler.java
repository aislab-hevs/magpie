package ch.hevs.aislab.magpie.sensor;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import ch.hevs.aislab.magpie.event.MagpieEvent;

public abstract class SensorHandler extends Handler implements SensorConnection {

    private final String TAG = getClass().getName();

    private SensorService mSensorService;

    private static final int START_CONNECTION = 1;
    private static final int STOP_CONNECTION = 0;
    public static final int SEND_MESSAGE = 2;


    public SensorHandler(Looper looper) {
        super(looper);
    }

    public static Message makeStartConnectionMessage(Intent intent) {
        Message message = Message.obtain();
        message.obj = intent;
        message.arg1 = START_CONNECTION;
        return message;
    }

    public static Message makeStopConnectionMessage(Looper sensorServiceLooper) {
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
        } else if (type == SEND_MESSAGE) {
            MagpieEvent ev = processSensorMessage(message);
            mSensorService.sendEvent(ev);
        }
    }

    private void connectToSensorAndReply(Intent intent) {
        // Extract the Messenger from the Intent
        Messenger messenger = (Messenger) intent.getExtras().get(SensorService.REPLY_MESSENGER);
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
        mSensorService.bindToMagpieService();
    }


    protected SensorService getSensorService() {
        return mSensorService;
    }

    public void setSensorService(SensorService sensorService) {
        mSensorService = sensorService;
    }
}
