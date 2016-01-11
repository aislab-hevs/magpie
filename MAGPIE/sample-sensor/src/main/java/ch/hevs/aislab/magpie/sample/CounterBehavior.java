package ch.hevs.aislab.magpie.sample;


import android.content.Context;
import android.widget.Toast;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.behavior.Behavior;
import ch.hevs.aislab.magpie.event.MagpieEvent;

public class CounterBehavior extends Behavior {

    private int count;

    public CounterBehavior(Context context, MagpieAgent agent, int priority) {
        count = 0;
        setContext(context);
        setAgent(agent);
        setPriority(priority);
    }

    @Override
    public void action(MagpieEvent event) {
        count++;
        MainActivity activity = (MainActivity) getContext();
        //run the result directly on the UI thread - no need to pass the alert back to the activity
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name = getAgent().getName();
                Toast.makeText(
                        getContext(),
                        "Agent '" + name + "' received " + count + " events",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean isTriggered(MagpieEvent event) {
        return true;
    }

}
