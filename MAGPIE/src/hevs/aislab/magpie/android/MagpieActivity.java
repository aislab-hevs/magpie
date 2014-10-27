package hevs.aislab.magpie.android;

import hevs.aislab.magpie.android.MagpieService.MagpieBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

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
		Log.i(TAG, "onStop()");
		unbindService(this);
		super.onStop();
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		Log.i(TAG, "onServiceConnected()");
		mService = ((MagpieBinder) service).getService();
		onEnvironmentConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {

	}
}
