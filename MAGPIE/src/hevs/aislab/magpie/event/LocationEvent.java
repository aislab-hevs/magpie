package hevs.aislab.magpie.event;

import hevs.aislab.magpie.environment.Services;
import android.location.Location;

public class LocationEvent extends MagpieEvent {

	private Location location;
	
	public LocationEvent(Location location) {
		this.type = Services.GPS_LOCATION;
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
}
