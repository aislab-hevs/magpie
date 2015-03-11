package ch.hevs.aislab.magpie.activity;


import android.test.ActivityInstrumentationTestCase2;

import ch.hevs.aislab.magpie.environment.Environment;

public class ActivityTest extends ActivityInstrumentationTestCase2<MagpieActivityTest>{

    private MagpieActivityTest magpieActivityTest;

    public ActivityTest() {
        super(MagpieActivityTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        magpieActivityTest = getActivity();
    }

    public void testPreConditions() {
        assertNotNull("magpieActivity is null", magpieActivityTest);
    }

    public void testAddAgents() {
        // First time: the agent is registered from the activity
        int numAgents = Environment.getInstance().getRegisteredAgents().size();
        assertEquals(1,numAgents);
        magpieActivityTest.finish();

        // Second time: the agent is registered from the service
        magpieActivityTest = getActivity();
        numAgents = Environment.getInstance().getRegisteredAgents().size();
        assertEquals(1,numAgents);
        magpieActivityTest.finish();

    }
}
