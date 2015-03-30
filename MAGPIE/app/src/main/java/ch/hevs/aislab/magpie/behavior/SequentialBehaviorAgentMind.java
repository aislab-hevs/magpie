package ch.hevs.aislab.magpie.behavior;


public class SequentialBehaviorAgentMind extends BehaviorAgentMind {

    public void executeBehaviors() {
        for (Behavior b : triggeredBehaviors) {
            b.action(event);
        }
        triggeredBehaviors.clear();
    }
}
