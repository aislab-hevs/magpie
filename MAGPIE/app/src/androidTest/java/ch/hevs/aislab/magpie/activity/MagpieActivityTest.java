package ch.hevs.aislab.magpie.activity;

import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.environment.Services;

public class MagpieActivityTest extends MagpieActivity {

    @Override
    public void onEnvironmentConnected() {

        MagpieAgent agent = new MagpieAgent("monitoring agent", Services.LOGIC_TUPLE);
        mService.registerAgent(agent);

    }
}
