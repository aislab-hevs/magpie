package ch.hevs.aislab.magpie.behavior;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.magpie.agent.IAgentMind;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public abstract class BehaviorAgentMind implements IAgentMind, Serializable {

    protected List<Behavior> behaviors;
    protected List<Behavior> triggeredBehaviors;
    protected transient MagpieEvent event;


    public BehaviorAgentMind() {
        behaviors = new ArrayList<>();
        triggeredBehaviors = new ArrayList<>();
    }

    public void addBehavior(Behavior b) {
        behaviors.add(b);
    }

    public List<Behavior> getBehaviors() {
        return behaviors;
    }

    @Override
    public void updatePerception(MagpieEvent event) {
        this.event = event;
    }

    @Override
    public MagpieEvent produceAction(long timestamp) {
        for (Behavior b : behaviors) {
            if (b.isTriggered(event)) {
                triggeredBehaviors.add(b);
            }
        }
        executeBehaviors();
        return null;
    }

    protected abstract void executeBehaviors();
}