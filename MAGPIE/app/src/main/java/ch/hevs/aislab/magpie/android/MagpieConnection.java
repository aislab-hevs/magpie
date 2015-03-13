package ch.hevs.aislab.magpie.android;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;

public interface MagpieConnection {

	public void onEnvironmentConnected();

    public void onAlertProduced(LogicTupleEvent alert);
	
}
