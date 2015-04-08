package ch.hevs.aislab.magpie.sample;


import android.content.Context;
import android.widget.Toast;

import ch.hevs.aislab.magpie.behavior.Behavior;
import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class HighWeightBehaviour extends Behavior {

    public HighWeightBehaviour(Context context, MagpieAgent agent, int priority) {
        setContext(context);
        setAgent(agent);
        setPriority(priority);
    }

    @Override
    public void action(MagpieEvent event) {
        LogicTupleEvent lte = (LogicTupleEvent) event;
        long value = Long.parseLong(lte.getArguments().get(0));
        if (value > 90) {
            ((MainActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = getAgent().getName();
                    Toast.makeText(
                            getContext(),
                            "Agent '" + name + "' detected a high weight",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean isTriggered(MagpieEvent event) {
        LogicTupleEvent condition = (LogicTupleEvent) event;
        return condition.getName().equals("weight");
    }
}
