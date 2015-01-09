package ch.hevs.aislab.magpie.context;

import ch.hevs.aislab.magpie.android.MagpieService;
import ch.hevs.aislab.magpie.environment.Environment;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LocationEvent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSContextEntity extends ContextEntity implements LocationListener {

private final static String TAG = "Magpie-GPSContextEntity";
	
	private static final String service = Services.GPS_LOCATION;
	
	private LocationManager locationManager;
	
	private boolean isGPSEnabled;
	// private boolean isSynchronous; // Modify how the GPS readings are processed
	// Asynchronous: every new reading is sent to the environment
	// Synchronous: the environment asks for the last known location
	
	private long pollingFreq;
	private float minDistance;
	
	public GPSContextEntity() {
		this(10 * 1000,0);
	}
	
	public GPSContextEntity(long pollingFreq, float minDistance) {
		super(service);
		this.pollingFreq = pollingFreq;
		this.minDistance = minDistance;
		
		locationManager = (LocationManager) MagpieService
				.getContext()
				.getSystemService(Context.LOCATION_SERVICE);
		
		isGpsProviderEnabled();
	}
	
	public boolean isGpsProviderEnabled() {
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isGPSEnabled;
	}
	
	/*
	private void showAlertDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getMagpieActivity());
		final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		final String title = "Enable GPS"; // Can be changed by a String from resources
		final String message = "GPS is not enabled. Do you want to go to settings menu?";
		
		builder.setTitle(title)
			.setMessage(message)
			.setPositiveButton("Settings",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int id) {
							Intent intent = new Intent(action);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							getMagpieActivity().startActivity(intent);
						}
					})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int id) {
							d.dismiss();
						}
					});
		builder.show();
	}
	*/
	
	public void init() {
		Log.i(TAG, "GPSContextEntity  - init()");
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				pollingFreq, 
				minDistance, 
				this);
	}
	
	public void stop() {
		Log.i(TAG, "GPSContextEntity  - stop()");
		locationManager.removeUpdates(this);
	}
	
	// Methods to get and set the configuration settings

	/* Methods from the LocationListener interface */
	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "onLocationChanged()");
		LocationEvent event = new LocationEvent(location);
		Environment.getInstance().registerEvent(event);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(TAG, "GPSContextEntity - onProviderDisabled(String provider)");		
		//showAlertDialog();
		//if (!getEnvironment().getAgentsInterests().containsKey(Services.GPS_LOCATION)) {
			//stop();
		//}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(TAG, "GPSContextEntity - onProviderEnabled(String provider)");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
}
