package ch.hevs.aislab.magpie.sample.activity;

import android.test.ActivityInstrumentationTestCase2;

import ch.hevs.aislab.magpie.environment.Environment;
import ch.hevs.aislab.magpie.sample.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity magpieActivityTest;

    public MainActivityTest() {
        super(MainActivity.class);
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
        assertEquals(2,numAgents);
        magpieActivityTest.finish();

        // Second time: the agent is registered from the service
        magpieActivityTest = getActivity();
        numAgents = Environment.getInstance().getRegisteredAgents().size();
        assertEquals(2,numAgents);
        magpieActivityTest.finish();

    }
}
