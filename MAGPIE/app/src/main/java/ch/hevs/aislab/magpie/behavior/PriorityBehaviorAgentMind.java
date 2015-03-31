package ch.hevs.aislab.magpie.behavior;

import java.util.Collections;

public class PriorityBehaviorAgentMind extends BehaviorAgentMind {

    @Override
    protected void executeBehaviors() {
        Collections.sort(triggeredBehaviors, Collections.reverseOrder());
        for (Behavior b : triggeredBehaviors) {
            b.action(event);
        }
        triggeredBehaviors.clear();
    }
}
