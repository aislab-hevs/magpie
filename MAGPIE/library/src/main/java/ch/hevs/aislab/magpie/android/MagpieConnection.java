package ch.hevs.aislab.magpie.android;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;

public interface MagpieConnection {

	void onEnvironmentConnected();

    void onAlertProduced(LogicTupleEvent alert);
	
}
