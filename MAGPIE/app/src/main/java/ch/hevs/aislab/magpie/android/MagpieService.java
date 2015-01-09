package ch.hevs.aislab.magpie.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.context.ContextEntity;
import ch.hevs.aislab.magpie.environment.Environment;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class MagpieService extends Service {

	/**
	 * Used for debugging
	 */
	private final String TAG = getClass().getName(); 
	
	private final IBinder mBinder = new MagpieBinder();
	
	private Environment mEnvironment;
	
	private static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG, "onCreate()");
		if (mEnvironment != null) {
			Log.i(TAG, "Environment instance: " + mEnvironment.toString());
		}		
		mEnvironment = Environment.getInstance();
		//AndroidContext.getInstance().setContext(this.getBaseContext());
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		// Destroy the thread running the Environment life-cycle
		//mEnvironment.shutDown();
		
		// Remove all the resources form the environment
		//if (mEnvironment.getRegisteredContextEntities().containsKey(Services.GPS_LOCATION)) {
			// ((GPSContextEntity) mEnvironment.getRegisteredContextEntities().get(Services.GPS_LOCATION)).pause();
		//}
	}
	
	/**
	 * Binder object returned to the caller
	 */
	public class MagpieBinder extends Binder {
		public MagpieService getService() {
			return MagpieService.this;
		}
	}
	
	/**
	 * Factory method to make an intent to connect with this service
	 */
	public static Intent makeIntent(Context context) {
		mContext = context;
		return new Intent(context, MagpieService.class);
	}
	
	public static Context getContext() {
		return mContext;
	}
	
	/**
	 * Actions that can be performed in the Environment from an Activity
	 */
	public void registerAgent(MagpieAgent agent) {
		mEnvironment.registerAgent(agent);
	}
	
	public void registerEvent(MagpieEvent event) {
		mEnvironment.registerEvent(event);
	}
	
	public ContextEntity getContextEntity(String service) {
		return mEnvironment.getContextEntity(service);
	}
}
