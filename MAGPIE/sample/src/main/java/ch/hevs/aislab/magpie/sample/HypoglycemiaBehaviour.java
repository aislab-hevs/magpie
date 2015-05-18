package ch.hevs.aislab.magpie.sample;

import android.content.Context;
import android.widget.Toast;

import ch.hevs.aislab.magpie.behavior.Behavior;
import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class HypoglycemiaBehaviour extends Behavior {

    private static final double HYPO_THRESHOLD = 4;

    public HypoglycemiaBehaviour(Context context, MagpieAgent agent, int priority) {
        setContext(context);
        setAgent(agent);
        setPriority(priority);
    }

    @Override
    public void action(MagpieEvent event) {
        LogicTupleEvent lte = (LogicTupleEvent) event;
        double value = Double.parseDouble(lte.getArguments().get(0));
        if (value < HYPO_THRESHOLD) {
            ((MainActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String name = getAgent().getName();
                    Toast.makeText(
                            getContext(),
                            "Agent '" + name + "' detected an hypoglycemia",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean isTriggered(MagpieEvent event) {
        LogicTupleEvent condition = (LogicTupleEvent) event;
        return condition.getName().equals("glucose");
    }
}