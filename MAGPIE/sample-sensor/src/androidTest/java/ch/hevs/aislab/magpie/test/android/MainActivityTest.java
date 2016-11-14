package ch.hevs.aislab.magpie.test.android;

import android.test.ActivityInstrumentationTestCase2;

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

    }
}
