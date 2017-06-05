package ch.hevs.aislab.paams.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.connector.DoubleValueAdapter;
import ch.hevs.aislab.paams.connector.SingleValueAdapter;
import ch.hevs.aislab.paams.connector.ValueAdapter;
import ch.hevs.aislab.paams.connector.ValueDAO;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;


public class ListValuesFragment extends ListFragment implements MainActivity.OnChangeDummyDataDisplayListener {

    private static final String TAG = "ListValuesFragment";

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";
    private static final String BUNDLE_KEY = "items";

    private Type type;
    private static ValueAdapter valueAdapter;
    private ValueDAO valueDAO;
    private FloatingActionButton addValueButton;
    private Boolean displayDummyData;


    public ListValuesFragment() {

    }

    public static ListValuesFragment newInstance(Type type) {
        ListValuesFragment fragment = new ListValuesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        Log.i(TAG, "onAttach()");
        displayDummyData = ((MainActivity) getActivity()).setOnChangeDummyDataDisplayListener(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }

        Log.i(TAG, "onCreate() from " + type);

        if (savedInstanceState == null) {
            switch (type) {
                case GLUCOSE:
                case WEIGHT:
                    valueAdapter = new SingleValueAdapter();
                    break;
                case BLOOD_PRESSURE:
                    valueAdapter = new DoubleValueAdapter();
                    break;
            }
        } else {
            if (savedInstanceState.getParcelableArray(BUNDLE_KEY) != null) {
                Value[] values = (Value[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
                List<Value> valuesList = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    valuesList.add(values[i]);
                }
                switch (type) {
                    case GLUCOSE:
                    case WEIGHT:
                        valueAdapter = new SingleValueAdapter(valuesList);
                        break;
                    case BLOOD_PRESSURE:
                        valueAdapter = new DoubleValueAdapter(valuesList);
                        break;
                }
            }
        }

        valueDAO = new ValueDAO(getActivity());
        valueDAO.open();
        if (valueAdapter.getCount() == 0) {
            valueAdapter.addAllItems(valueDAO.getAllValues(type));
        }
        setListAdapter(valueAdapter);
        valueAdapter.displayDummyData(displayDummyData);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart() from " + type);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume() from " + type);
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause() from " + type);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop() from " + type);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_values, container, false);
        addValueButton = (FloatingActionButton) view.findViewById(R.id.fab_addValue);
        setAddAction(addValueButton);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated() from " + type);

        getListView().setHeaderDividersEnabled(true);
        LinearLayout headerLayout = null;
        switch (type) {
            case GLUCOSE:
            case WEIGHT:
                headerLayout = (LinearLayout) LayoutInflater
                        .from(getActivity()).inflate(R.layout.single_value_row_header, getListView(), false);
                TextView valueHeaderTextView = (TextView) headerLayout.findViewById(R.id.valueHeaderTextView);
                if (type.equals(Type.GLUCOSE)) {
                    valueHeaderTextView.setText("mmol/L");
                } else if (type.equals(Type.WEIGHT)) {
                    valueHeaderTextView.setText("kg");
                }
                break;
            case BLOOD_PRESSURE:
                headerLayout = (LinearLayout) LayoutInflater
                        .from(getActivity()).inflate(R.layout.double_value_row_header, getListView(), false);
                TextView firstValueHeaderTextView = (TextView) headerLayout.findViewById(R.id.firstValueHeaderTextView);
                firstValueHeaderTextView.setText("Systolic");
                TextView secondValueHeaderTextView = (TextView) headerLayout.findViewById(R.id.secondValueHeaderTextView);
                secondValueHeaderTextView.setText("Diastolic");
                break;
        }

        getListView().addHeaderView(headerLayout, null, false);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Value value = (Value) valueAdapter.getItem(--position);
                ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
                if (value.isMarked()) {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
                    value.setMarked(false);
                } else {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_on);
                    value.setMarked(true);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        //TODO: Save the marked state
        Log.i(TAG, "onDestroy() from " + type);
        valueDAO.close();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Value[] items = valueAdapter.getItems();
        Log.i(TAG, "onSaveInstanceState() from " + type);
        Log.i(TAG, "Number of items: " + items.length);
        if (items.length > 0) {
            outState.putParcelableArray(BUNDLE_KEY, items);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Log.i(TAG, "Fragment type in onCreateOptionsMenu(): " + type.name());
        menu.clear();
        menuInflater.inflate(R.menu.menu_delete_value_toolbar, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Fragment type: " + type.name());
        switch (item.getItemId()) {
            case R.id.deleteValueButton:
                List<Value> values = valueAdapter.getSelectedItems();
                if (values.isEmpty()) {
                    Toast.makeText(getContext(), "Select the items to delete", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    int size = 0;
                    String dummy = "";
                    for (Value value : values) {
                        if (!value.isDummy()) {
                            size++;
                            valueAdapter.removeItem(value);
                            valueDAO.deleteValue(value);
                        } else {
                            dummy += " Dummy data cannot be deleted.";
                        }
                    }
                    Toast.makeText(getContext(), "Deleted " + size + " entries." + dummy, Toast.LENGTH_LONG).show();
                    return true;
                }
            default:
                return false;
        }
    }

    public ValueAdapter getValueAdapter() {
        return valueAdapter;
    }

    private void setAddAction(FloatingActionButton button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_container, AddValueFragment.newInstance(type), "Add").commit();
            }
        });
    }

    @Override
    public void displayDummyData(Boolean displayDummyData) {
        Log.i(TAG, "displayDummyData() with " + displayDummyData);
        valueAdapter.displayDummyData(displayDummyData);
    }
}
