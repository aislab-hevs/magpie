package ch.hevs.aislab.paams.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ch.hevs.aislab.paams.connector.DoubleValueDAO;
import ch.hevs.aislab.paams.connector.SingleValueDAO;
import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;


public class AddValueFragment extends Fragment {

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";

    private Type type;
    private SingleValueDAO singleValueDAO;
    private DoubleValueDAO doubleValueDAO;

    private OnAddedNewMeasurementListener callback;

    public interface OnAddedNewMeasurementListener {
        void addSingleValue(SingleValue measurement);
        void addDoubleValue(DoubleValue measurement);
    }


    public AddValueFragment() {

    }

    public static AddValueFragment newInstance(Type type) {
        AddValueFragment fragment = new AddValueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (OnAddedNewMeasurementListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAddedNewMeasurementListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }

        singleValueDAO = new SingleValueDAO(getActivity());
        doubleValueDAO = new DoubleValueDAO(getActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        TextInputLayout firstValueTextInputLayout;
        TextInputLayout secondValueTextInputLayout; // Used for the diastolic blood pressure
        EditText dateEditText = null;
        EditText timeEditText = null;
        Button cancelButton;
        Button acceptButton;
        switch (type) {
            case GLUCOSE:
                // Instantiate the UI elements
                view = inflater.inflate(R.layout.fragment_add_single_value, container, false);
                firstValueTextInputLayout = (TextInputLayout) view.findViewById(R.id.singleValueTextInputLayout);
                dateEditText = (EditText) view.findViewById(R.id.dateEditText);
                timeEditText = (EditText) view.findViewById(R.id.timeEditText);
                cancelButton = (Button) view.findViewById(R.id.cancelButton);
                acceptButton = (Button) view.findViewById(R.id.acceptButton);
                // Set actions to the UI elements
                firstValueTextInputLayout.setHint("Value [mmol/L]");
                removeFocus(firstValueTextInputLayout);
                setCancelAction(cancelButton, dateEditText, timeEditText, firstValueTextInputLayout);
                setAcceptAction(acceptButton, dateEditText, timeEditText, firstValueTextInputLayout);
                break;
            case BLOOD_PRESSURE:
                view = inflater.inflate(R.layout.fragment_add_double_value, container, false);
                firstValueTextInputLayout = (TextInputLayout) view.findViewById(R.id.firstValueTextInputLayout);
                secondValueTextInputLayout = (TextInputLayout) view.findViewById(R.id.secondValueTextInputLayout);
                dateEditText = (EditText) view.findViewById(R.id.dateEditText);
                timeEditText = (EditText) view.findViewById(R.id.timeEditText);
                cancelButton = (Button) view.findViewById(R.id.cancelButton);
                acceptButton = (Button) view.findViewById(R.id.acceptButton);
                firstValueTextInputLayout.setHint("Systolic [mmHg]");
                secondValueTextInputLayout.setHint("Diastolic [mmHg]");
                removeFocus(firstValueTextInputLayout, secondValueTextInputLayout);
                setCancelAction(cancelButton, dateEditText, timeEditText, firstValueTextInputLayout, secondValueTextInputLayout);
                setAcceptAction(acceptButton, dateEditText, timeEditText, firstValueTextInputLayout, secondValueTextInputLayout);
                break;
            case WEIGHT:
                view = inflater.inflate(R.layout.fragment_add_single_value, container, false);
                firstValueTextInputLayout = (TextInputLayout) view.findViewById(R.id.singleValueTextInputLayout);
                dateEditText = (EditText) view.findViewById(R.id.dateEditText);
                timeEditText = (EditText) view.findViewById(R.id.timeEditText);
                cancelButton = (Button) view.findViewById(R.id.cancelButton);
                acceptButton = (Button) view.findViewById(R.id.acceptButton);
                firstValueTextInputLayout.setHint("Value [kg]");
                removeFocus(firstValueTextInputLayout);
                setCancelAction(cancelButton, dateEditText, timeEditText, firstValueTextInputLayout);
                setAcceptAction(acceptButton, dateEditText, timeEditText, firstValueTextInputLayout);
                break;
        }
        setDatePicker(dateEditText);
        setTimePicker(timeEditText);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        singleValueDAO.open();
        doubleValueDAO.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        singleValueDAO.close();
        doubleValueDAO.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setDatePicker(final EditText dateEditText) {
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year;
                int month;
                int day;
                String dateString = dateEditText.getText().toString();
                if (dateString.isEmpty()) {
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                } else {
                    year = Integer.parseInt(dateString.substring(6,10));
                    month = Integer.parseInt(dateString.substring(3,5));
                    month--;
                    day = Integer.parseInt(dateString.substring(0,2));
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        dateEditText.setText(sdf.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void setTimePicker(final EditText timeEditText) {
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int hour;
                int minute;
                String timeString = timeEditText.getText().toString();
                if (timeString.isEmpty()) {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                } else {
                    hour = Integer.parseInt(timeString.substring(0,2));
                    minute = Integer.parseInt(timeString.substring(3,5));
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        timeEditText.setText(sdf.format(calendar.getTime()));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

    }

    private void setCancelAction(Button cancelButton, final EditText dateEditText,
                                 final EditText timeEditText, final TextInputLayout ... textInputLayouts) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextInputLayout textInputLayout : textInputLayouts) {
                    textInputLayout.getEditText().setText("");
                    textInputLayout.clearFocus();
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputLayout.getEditText().getWindowToken(), 0);
                }
                dateEditText.setText("");
                timeEditText.setText("");
            }
        });
    }

    private void setAcceptAction(Button acceptButton, final EditText dateEditText,
                                 final EditText timeEditText, final TextInputLayout ... textInputLayouts) {
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate the values
                if (!areValuesValid(dateEditText, timeEditText, textInputLayouts)) {
                    return;
                }

                switch (type) {
                    case GLUCOSE:
                    case WEIGHT:
                        SingleValue newSingleValue = newSingleValueInstance(textInputLayouts[0], dateEditText, timeEditText);
                        // Store the new glucose measurement in the database
                        singleValueDAO.createSingleValue(newSingleValue);
                        // Send it to the ListValuesFragment
                        callback.addSingleValue(newSingleValue);
                        break;
                    case BLOOD_PRESSURE:
                        DoubleValue newDoubleValue = newDoubleValueInstance(dateEditText, timeEditText, textInputLayouts);
                        doubleValueDAO.createDoubleValue(newDoubleValue);
                        callback.addDoubleValue(newDoubleValue);
                        break;
                }

                // Remove the values from the UI elements
                for (TextInputLayout textInputLayout : textInputLayouts) {
                    textInputLayout.getEditText().setText("");
                    textInputLayout.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputLayout.getWindowToken(), 0);
                }
                dateEditText.setText("");
                timeEditText.setText("");
            }
        });
    }

    private void removeFocus(TextInputLayout ... textInputLayouts) {
        for (TextInputLayout textInputLayout: textInputLayouts) {
            textInputLayout.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        textView.clearFocus();
                        // Hide the keyboard
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    }
                    return false;
                }
            });
        }
    }

    private boolean areValuesValid(EditText dateEditText, EditText timeEditText,
                                   TextInputLayout ... textInputLayouts) {
        for (TextInputLayout textInputLayout : textInputLayouts) {
            String value = textInputLayout.getEditText().getText().toString();
            if (value.isEmpty()) {
                Toast.makeText(getActivity(), "Enter the value of the measurement", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (dateEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter the date of the measurement", Toast.LENGTH_LONG).show();
            return false;
        }
        if (timeEditText.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Enter the time of the measurement", Toast.LENGTH_LONG).show();
            return false;
        }
        long timestamp = convertDateTimeToTimestamp(dateEditText, timeEditText);
        if (timestamp > System.currentTimeMillis()) {
            Toast.makeText(getActivity(), "Enter a date/time that is not in the future", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private SingleValue newSingleValueInstance(TextInputLayout textInputLayout, EditText dateEditText,
                                               EditText timeEditText) {
        String valueString = textInputLayout.getEditText().getText().toString();
        double value = Double.valueOf(valueString);
        long timestamp = convertDateTimeToTimestamp(dateEditText, timeEditText);
        SingleValue singleValue = new SingleValue();
        singleValue.setValue(value);
        singleValue.setTimestamp(timestamp);
        singleValue.setType(type);
        return singleValue;
    }

    private DoubleValue newDoubleValueInstance(EditText dateEditText, EditText timeEditText,
                                               TextInputLayout ... textInputLayouts) {
        String firstValueString = textInputLayouts[0].getEditText().getText().toString();
        String secondValueString = textInputLayouts[1].getEditText().getText().toString();
        int firstValue = Integer.valueOf(firstValueString);
        int secondValue = Integer.valueOf(secondValueString);
        long timestamp = convertDateTimeToTimestamp(dateEditText, timeEditText);
        DoubleValue doubleValue = new DoubleValue();
        doubleValue.setFirstValue(firstValue);
        doubleValue.setSecondValue(secondValue);
        doubleValue.setTimestamp(timestamp);
        doubleValue.setType(Type.BLOOD_PRESSURE);
        return doubleValue;
    }

    private long convertDateTimeToTimestamp(EditText dateEditText, EditText timeEditText) {
        String dateString = dateEditText.getText().toString();
        String timeString = timeEditText.getText().toString();
        String timestampString = dateString + " " + timeString;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault());
        long timestamp = 0;
        try {
            Date parsedDate = dateFormat.parse(timestampString);
            timestamp = parsedDate.getTime();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return timestamp;
    }
}
