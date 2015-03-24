package ch.hevs.aislab.magpie.behavior;

import ch.hevs.aislab.magpie.event.MagpieEvent;

public interface IBehavior {

    public void action(MagpieEvent event);

    public boolean isTriggered(MagpieEvent event);

}
