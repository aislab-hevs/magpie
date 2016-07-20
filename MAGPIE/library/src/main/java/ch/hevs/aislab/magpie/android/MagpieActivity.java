package ch.hevs.aislab.magpie.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.android.MagpieService.MagpieBinder;
import ch.hevs.aislab.magpie.environment.Environment;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;
import ch.hevs.aislab.magpie.sensor.SensorHandler;
import ch.hevs.aislab.magpie.sensor.SensorService;

public abstract class MagpieActivity extends AppCompatActivity implements MagpieConnection {


	private final String ACTIVITY_NAME = getClass().getName();

    static final String ACTION_ONE_WAY_COMM = "ch.hevs.aislab.magpie.android.ONE_WAY";
    public static final String ACTION_TWO_WAY_COMM = "ch.hevs.aislab.magpie.android.TWO_WAYS";

    public static final String MAGPIE_EVENT = "event";

    // Used for one way communications with the service
    private MagpieService mService;

    // Used for two way communications with the service
    private Messenger requestMessenger;
    private Messenger replyMessenger = new Messenger(new ReplyHandler(this));

    private Intent sensorServiceIntent;

    // Sensor related fields
    private SensorReplyHandler sensorReplyHandler;

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(ACTIVITY_NAME, "onStart()");
		// Bind to the Service running the Environment

        // Bind for interactions not requiring an answer from the service
        Intent intentOneWay = MagpieService.makeIntent(this);
        intentOneWay.setAction(ACTION_ONE_WAY_COMM);
		bindService(intentOneWay, oneWayConnection, Context.BIND_AUTO_CREATE);

        // Bind for interactions requiring an answer from the service
        Intent intentTwoWays = MagpieService.makeIntent(this);
        intentTwoWays.setAction(ACTION_TWO_WAY_COMM);
        bindService(intentTwoWays, twoWayConnection, Context.BIND_AUTO_CREATE);

	}
	
	@Override
	protected void onStop() {
        super.onStop();
        Log.i(ACTIVITY_NAME, "onStop()");
        unbindService(oneWayConnection);
        unbindService(twoWayConnection);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "onDestroy()");
        if (sensorServiceIntent != null) {
            stopService(sensorServiceIntent);
        }
    }

    private ServiceConnection oneWayConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MagpieBinder) service).getService();

            SharedPreferences settings = getSharedPreferences(MagpieService.MAGPIE_PREFS, MODE_PRIVATE);
            Set<String> agentNames = settings.getStringSet(ACTIVITY_NAME, new HashSet<String>());

            if (agentNames.isEmpty()) {
                onEnvironmentConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

        private WeakReference<MagpieActivity> mActivity;

        public ReplyHandler(MagpieActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message reply) {
            MagpieActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }

            Bundle bundleAlert = reply.getData();
            LogicTupleEvent alert = bundleAlert.getParcelable(MAGPIE_EVENT);
            activity.onAlertProduced(alert);
        }
    }

    protected MagpieService getService() {
        return mService;
    }

    /**
     * This method sends an Event to the Environment, which is generated from the UI. The 'what' field
     * in the message specifies that it contains an Event, so that the Environment knows how to process
     * the message
     */
    public void sendEvent(MagpieEvent event) {

        Message request = Message.obtain();
        request.what = Environment.NEW_EVENT;
        request.replyTo = replyMessenger;

        Bundle bundle = new Bundle();
        bundle.putParcelable(MAGPIE_EVENT, event);

        request.setData(bundle);

        try {
            requestMessenger.send(request);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void registerAgent(MagpieAgent agent) {
        getService().registerAgent(agent, getClass().getName());
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

    protected void disconnectSensor() {
        sensorReplyHandler = null;
        stopService(sensorServiceIntent);
    }

    protected abstract void sensorConnectionResult(int code);

    /**
     * A nested class that uses the handleMessage() hook method to process Messages
     * sent to it from the SensorService
     */
    private static class SensorReplyHandler extends Handler {

        private WeakReference<MagpieActivity> mActivity;

        public SensorReplyHandler(MagpieActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        public void handleMessage(Message message) {
            int result = message.arg1;
            MagpieActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            activity.sensorConnectionResult(result);
        }
    }
}
