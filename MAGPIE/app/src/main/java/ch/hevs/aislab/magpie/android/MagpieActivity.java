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
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import ch.hevs.aislab.magpie.android.MagpieService.MagpieBinder;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public abstract class MagpieActivity extends FragmentActivity implements MagpieConnection {

	/**
	 * Used for debugging
	 */
	private final String TAG = getClass().getName();

    static final String ACTION_ONE_WAY_COMM = "ch.hevs.aislab.magpie.android.ONE_WAY";
    static final String ACTION_TWO_WAY_COMM = "ch.hevs.aislab.magpie.android.TWO_WAYS";

    static final String MAGPIE_EVENT = "event";

    // Used for one way communications with the service
    private MagpieService mService;

    // Used for two way communications with the service
    private Messenger requestMessenger;
    private Messenger replyMessenger = new Messenger(new ReplyHandler());

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart()");
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
        Log.i(TAG, "onStop()");
        unbindService(oneWayConnection);
	}

    private ServiceConnection oneWayConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MagpieBinder) service).getService();

            SharedPreferences settings = getSharedPreferences(MagpieService.MAGPIE_PREFS, MODE_PRIVATE);
            boolean firstTime = settings.getBoolean(TAG, true);
            if (firstTime) {
                onEnvironmentConnected();
            }

            /**
             * Register this Activity as bounded in SharedPreferences, so that in the next
             * binding the method onEnvironmentConnected() is not called again
             */
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(TAG, false);
            editor.commit();
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
            requestMessenger = null;
        }
    };

    private class ReplyHandler extends Handler {
        @Override
        public void handleMessage(Message reply) {
            Bundle bundleAlert = reply.getData();
            LogicTupleEvent alert = bundleAlert.getParcelable(MAGPIE_EVENT);
            onAlertProduced(alert);
        }
    }

    protected MagpieService getService() {
        return mService;
    }

    protected void sendEvent(MagpieEvent event) {

        Message request = Message.obtain();
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
}
