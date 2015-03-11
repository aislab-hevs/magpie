package ch.hevs.aislab.magpie.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import ch.hevs.aislab.indexer.StringECKDTreeIndexer;
import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;
import hevs.aislab.magpie.R;

public class MagpieActivityTest extends MagpieActivity {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private EditText glucoseEditTxt;

    private PhysioTimestamp glucoseTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magpieactivitytest_layout);

        setTitle("MAGPIE example Application");
        glucoseEditTxt = (EditText) findViewById(R.id.glucoseEditTxt);
        glucoseTimestamp = new PhysioTimestamp();
    }

    @Override
    public void onEnvironmentConnected() {

        // Create a new Agent and register it into the environment
        MagpieAgent agent = new MagpieAgent("monitoring_agent", Services.LOGIC_TUPLE);
        PrologAgentMind mind = new PrologAgentMind(getApplicationContext(), new StringECKDTreeIndexer());
        agent.setMind(mind);
        mService.registerAgent(agent);
    }

    public void sendGlucoseEvent(View view) {
        String value = glucoseEditTxt.getText().toString();
        if (value.equals("")) {
            return;
        }
        LogicTupleEvent lte = new LogicTupleEvent("glucose", value);
        lte.setTimeStamp(glucoseTimestamp.timestamp.toDate().getTime());
        mService.registerEvent(lte);

        glucoseEditTxt.setText("");
    }

    /**
     * The rest of the code is to show the time pickers to the user,
     * and makes use of the betterpickers library
     */

    /** Set the glucose timestamp */
    public void setGlucoseTimestamp(View view) {
        showCalendarDatePickerDialog(glucoseTimestamp);
    }

    private void showCalendarDatePickerDialog(PhysioTimestamp physioTimestamp) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(physioTimestamp, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    public class PhysioTimestamp implements
            CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

        private MutableDateTime timestamp = MutableDateTime.now();

        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog,
                              int year, int monthOfYear, int dayOfMonth) {
            timestamp.setDate(year, monthOfYear, dayOfMonth);
            DateTime now = DateTime.now();
            RadialTimePickerDialog radialTimePickerDialog = RadialTimePickerDialog
                    .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), true);
            radialTimePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        }

        @Override
        public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hourOfDay, int minute) {
            timestamp.setHourOfDay(hourOfDay);
            timestamp.setMinuteOfHour(minute);
        }
    }
}
