package ch.hevs.aislab.magpie.sensor;

import android.os.Message;

import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface SensorConnection {

    /**
     * Logic to establish a connection with a specific sensor
     * @return int representing the result of the connection
     */
    int onStartConnection();

    void onStopConnection();

    MagpieEvent processSensorMessage(Message message);

    void onAlertProduced(LogicTupleEvent alert);

}
