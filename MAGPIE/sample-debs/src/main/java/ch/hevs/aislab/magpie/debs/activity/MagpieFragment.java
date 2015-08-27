package ch.hevs.aislab.magpie.debs.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment.NumberPickerDialogHandler;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.event.LogicTupleEvent;

public class MagpieFragment extends Fragment implements View.OnClickListener {

    private static final String FRAG_TAG_DATE_PICKER = "datePickerDialogFragment";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";

    private TextView glucoseValueTxtView;
    private TextView glucoseDateTxtView;
    private TextView glucoseTimeTxtView;

    private PhysioMeasurement glucoseMeasurement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_magpie, container, false);

        glucoseValueTxtView = (TextView) v.findViewById(R.id.glucoseValueTxtView);
        glucoseDateTxtView = (TextView) v.findViewById(R.id.glucoseDateValueTxtView);
        glucoseTimeTxtView = (TextView) v.findViewById(R.id.glucoseTimeValueTxtView);
        glucoseMeasurement = new PhysioMeasurement(glucoseValueTxtView, glucoseDateTxtView, glucoseTimeTxtView, "mmol/l");

        Button btnValue = (Button) v.findViewById(R.id.glucoseValueBtn);
        btnValue.setOnClickListener(this);
        Button btnTimestamp = (Button) v.findViewById(R.id.glucoseTimestampBtn);
        btnTimestamp.setOnClickListener(this);
        Button btnSend = (Button) v.findViewById(R.id.glucoseSendBtn);
        btnSend.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.glucoseValueBtn:
                showNumberPicker(glucoseMeasurement, "Glucose (mmol/l)");
                break;
            case R.id.glucoseTimestampBtn:
                showCalendarDatePickerDialog(glucoseMeasurement);
                break;
            case R.id.glucoseSendBtn:
                processEvent("glucose", glucoseMeasurement);
                break;
            default:
                break;
        }
    }

    private void processEvent(String type, PhysioMeasurement measurement) {

        if (measurement.value == 0) {
            Toast.makeText(getActivity(), "Set the value of the measurement", Toast.LENGTH_LONG).show();
            return;
        }

        if (!measurement.isTimestampSet) {
            Toast.makeText(getActivity(), "Set the timestamp of the measurement", Toast.LENGTH_LONG).show();
            return;
        }

        String value = Double.toString(measurement.value);
        LogicTupleEvent lte = new LogicTupleEvent(type, value);
        lte.setTimeStamp(measurement.timestamp.toDate().getTime());
        ((PublisherActivity) getActivity()).sendEvent(lte);
    }

    private void showNumberPicker(NumberPickerDialogHandler pickerHandler, String label) {
        Log.d("MAGPIE", "Should show the NumberPicker");
        NumberPickerBuilder npb = new NumberPickerBuilder()
                .addNumberPickerDialogHandler(pickerHandler)
                .setFragmentManager(((PublisherActivity) getActivity()).getSupportFragmentManager())
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setPlusMinusVisibility(View.GONE)
                .setLabelText(label);
        npb.show();
    }

    private void showCalendarDatePickerDialog(PhysioMeasurement physioTimestamp) {
        FragmentManager fm = ((PublisherActivity) getActivity()).getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                .newInstance(physioTimestamp, now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth());
        calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
    }

    private class PhysioMeasurement implements NumberPickerDialogHandler,
            CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {

        private double value;
        private TextView valueTxtView;
        private MutableDateTime timestamp;
        private TextView dateTxtView;
        private TextView timeTxtView;
        private String units;

        private boolean isTimestampSet = false;

        public PhysioMeasurement(TextView valueTxtView, TextView dateTxtView, TextView timeTxtView, String units) {
            this.valueTxtView = valueTxtView;
            this.timestamp = MutableDateTime.now();
            this.dateTxtView = dateTxtView;
            this.timeTxtView = timeTxtView;
            this.units = units;
        }

        @Override
        public void onDialogNumberSet(int reference, int number, double decimal,
                                      boolean isNegative, double fullNumber) {
            value = fullNumber;
            valueTxtView.setText(fullNumber + " " + units);
        }

        @Override
        public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog,
                              int year, int monthOfYear, int dayOfMonth) {
            FragmentManager fm = ((PublisherActivity) getActivity()).getSupportFragmentManager();
            timestamp.setDate(year, monthOfYear + 1, dayOfMonth);
            DateTime now = DateTime.now();
            RadialTimePickerDialog radialTimePickerDialog = RadialTimePickerDialog
                    .newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(), true);
            radialTimePickerDialog.show(fm, FRAG_TAG_TIME_PICKER);

        }

        @Override
        public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hourOfDay, int minute) {
            timestamp.setHourOfDay(hourOfDay);
            timestamp.setMinuteOfHour(minute);
            isTimestampSet = true;
            DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
            DateTimeFormatter dtfTime = DateTimeFormat.forPattern("kk:mm");
            if (isToday(timestamp.toString(dtfDate))) {
                dateTxtView.setText("Today");
            } else {
                dateTxtView.setText(timestamp.toString(dtfDate));
            }
            timeTxtView.setText(timestamp.toString(dtfTime));
        }

        private boolean isToday(String otherDay) {
            MutableDateTime today = MutableDateTime.now();
            DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/MM/yyyy");
            return today.toString(dtfDate).equals(otherDay);
        }
    }
}
