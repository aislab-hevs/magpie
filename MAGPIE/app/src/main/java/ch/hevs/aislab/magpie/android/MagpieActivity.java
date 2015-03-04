package ch.hevs.aislab.magpie.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import ch.hevs.aislab.magpie.android.MagpieService.MagpieBinder;

public abstract class MagpieActivity extends Activity
		implements ServiceConnection, MagpieConnection {

	/**
	 * Used for debugging
	 */
	private final String TAG = getClass().getName();
		
	protected MagpieService mService;

	public MagpieActivity() {

	}
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart()");
		Intent intent = MagpieService.makeIntent(this);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
        unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.i(TAG, "onServiceConnected()");
		mService = ((MagpieBinder) service).getService();
		onEnvironmentConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
        Log.i(TAG,"onSeviceDisconnected()");
	}
}
