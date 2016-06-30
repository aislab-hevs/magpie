package ch.hevs.aislab.magpie.behavior;

import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IBehavior {

    void action(MagpieEvent event);

    boolean isTriggered(MagpieEvent event);

}
