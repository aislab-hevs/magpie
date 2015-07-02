package ch.hevs.aislab.magpie.event;

import android.location.Location;

import ch.hevs.aislab.magpie.environment.Services;

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
