package ch.hevs.aislab.magpie.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ch.hevs.aislab.indexer.StringECKDTreeIndexer;
import ch.hevs.aislab.magpie.agent.MagpieAgent;
import ch.hevs.aislab.magpie.agent.PrologAgentMind;
import ch.hevs.aislab.magpie.android.MagpieActivity;
import ch.hevs.aislab.magpie.environment.Services;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;


public class MainActivity extends MagpieActivity {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private EditText glucoseEditTxt;
    private TextView glucoseTstampTxtView;
    private EditText weightEditTxt;
    private TextView weightTstampTxtView;

    private PhysioTimestamp glucoseTstamp;
    private PhysioTimestamp weightTstamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("MAGPIE example Application");

        glucoseEditTxt = (EditText) findViewById(R.id.glucoseEditTxt);
        glucoseTstampTxtView = (TextView) findViewById(R.id.glucoseTimestampTxtView);
        weightEditTxt = (EditText) findViewById(R.id.weightEditTxt);
        weightTstampTxtView = (TextView) findViewById(R.id.weightTimestampTxtView);

        glucoseTstamp = new PhysioTimestamp(glucoseTstampTxtView);
        weightTstamp = new PhysioTimestamp(weightTstampTxtView);
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
        processEvent("glucose", glucoseEditTxt, glucoseTstamp, glucoseTstampTxtView);
    }

    public void sendWeightEvent(View view) {
        processEvent("weight", weightEditTxt, weightTstamp, weightTstampTxtView);
    }

    private void processEvent(String type, EditText editText, PhysioTimestamp timestamp, TextView textView) {
        // This is a boilerplate code
        String value = editText.getText().toString();
        if (value.equals("")) {
            Toast.makeText(this, "Can't send an empty value", Toast.LENGTH_LONG).show();
            return;
        }
        LogicTupleEvent lte = new LogicTupleEvent(type, value);
        lte.setTimeStamp(timestamp.timestamp.toDate().getTime());
        mService.registerEvent(lte);

        editText.setText("");
        editText.clearFocus();
        textView.setText("-");
    }

    /**
     * The rest of the code is to show the time pickers to the user,
     * and makes use of the betterpickers library
     */

    /** Set the glucose timestamp */
    public void setGlucoseTimestamp(View view) {
        showCalendarDatePickerDialog(glucoseTstamp);
    }

    /** Set the weight timestamp */
    public void setWeightTimestamp(View view) {
        showCalendarDatePickerDialog(weightTstamp);
    }

    private void showCalendarDatePickerDialog(PhysioTimestamp physioTimestamp) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(physioTimestamp, now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    private class PhysioTimestamp implements
            CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

        private MutableDateTime timestamp;
        private TextView textView;

        public PhysioTimestamp(TextView textView) {
            this.timestamp = MutableDateTime.now();
            this.textView = textView;
        }

        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog,
                              int year, int monthOfYear, int dayOfMonth) {
            timestamp.setDate(year, monthOfYear + 1, dayOfMonth);
            DateTime now = DateTime.now();
            RadialTimePickerDialog radialTimePickerDialog = RadialTimePickerDialog
                    .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), true);
            radialTimePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        }

        @Override
        public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hourOfDay, int minute) {
            timestamp.setHourOfDay(hourOfDay);
            timestamp.setMinuteOfHour(minute);
            DateTimeFormatter dtf = DateTimeFormat.forPattern("kk:mm dd/MM/yyyy");
            textView.setText(timestamp.toString(dtf));
        }
    }
}
