package ch.hevs.aislab.magpie.activity;


import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class ActivityTest extends ActivityInstrumentationTestCase2<MagpieActivityTest>{

    private MagpieActivityTest magpieActivityTest;

    public ActivityTest() {
        super(MagpieActivityTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        magpieActivityTest = getActivity();
    }

    public void testPreconditions() {
        assertNotNull("magpieActivity is null", magpieActivityTest);
        magpieActivityTest.finish();
        Log.i("MagpieActivityTest", "Last call...");
    }

    //public void testAgents() {
        //int numAgents = Environment.getInstance().getRegisteredAgents().size();
        //assertEquals(1,numAgents);

        //magpieActivityTest.finish();
        //magpieActivityTest = this.getActivity();

        //int agentsAfterDestroy = Environment.getInstance().getRegisteredAgents().size();
        //Log.i("ActivityTest", "Num. of agents: " + agentsAfterDestroy);
        //Log.i("ActivityTest", "" + Environment.getInstance().getRegisteredAgents().get(2).getId());
    //}
}
