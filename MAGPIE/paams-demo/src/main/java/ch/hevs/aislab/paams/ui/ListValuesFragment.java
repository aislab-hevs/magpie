package ch.hevs.aislab.paams.ui;

import android.content.Context;
import android.os.Bundle;
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
import ch.hevs.aislab.paams.connector.DoubleValueDAO;
import ch.hevs.aislab.paams.connector.SingleValueAdapter;
import ch.hevs.aislab.paams.connector.SingleValueDAO;
import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;


public class ListValuesFragment extends ListFragment {

    private static final String TAG = "ListValuesFragment";

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";
    private static final String BUNDLE_KEY = "items";

    private Type type;
    private static SingleValueAdapter singleValueAdapter;
    private SingleValueDAO singleValueDAO;
    private DoubleValueAdapter doubleValueAdapter;
    private DoubleValueDAO doubleValueDAO;

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
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }

        Log.i(TAG, "onCreate() from " + type);

        if (savedInstanceState != null) {
            switch (type) {
                case GLUCOSE:
                case WEIGHT:
                    SingleValue[] singleValues = (SingleValue[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
                    List<SingleValue> singleValueList = new ArrayList<>();
                    for (int i = 0; i < singleValues.length; i++) {
                        singleValueList.add(singleValues[i]);
                    }
                    singleValueAdapter = new SingleValueAdapter(singleValueList);
                    break;
            }
        } else {
            switch (type) {
                case GLUCOSE:
                case WEIGHT:
                    singleValueAdapter = new SingleValueAdapter();
                    break;
            }
        }

        switch (type) {
            case GLUCOSE:
            case WEIGHT:
                singleValueDAO = new SingleValueDAO(getActivity());
                singleValueDAO.open();
                if (singleValueAdapter.getCount() == 0) {
                    singleValueAdapter.addAllItems(singleValueDAO.getAllSingleValues(type));
                }
                setListAdapter(singleValueAdapter);
                break;
            case BLOOD_PRESSURE:
                doubleValueDAO = new DoubleValueDAO(getContext());
                doubleValueDAO.open();
                List<DoubleValue> listDoubleValues = doubleValueDAO.getAllDoubleValues();
                doubleValueAdapter = new DoubleValueAdapter(getContext(), listDoubleValues);
                setListAdapter(doubleValueAdapter);
                break;
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_values, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        }
        getListView().addHeaderView(headerLayout);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingleValue singleValue = (SingleValue) singleValueAdapter.getItem(--position);
                ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);

                if (singleValue.isMarked()) {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
                    singleValue.setMarked(false);
                } else {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_on);
                    singleValue.setMarked(true);
                }
            }
        });
    }

    @Override
    public void onPause() {
        switch (type) {
            case GLUCOSE:
            case WEIGHT:
                //TODO: Save the marked state
                singleValueDAO.close();
                break;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        switch (type) {
            case GLUCOSE:
            case WEIGHT:
                SingleValue[] items = singleValueAdapter.getItems();
                outState.putParcelableArray(BUNDLE_KEY, items);
                break;
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        switch (type) {
            case GLUCOSE:
                menuInflater.inflate(R.menu.menu_glucose_toolbar, menu);
                break;
            case BLOOD_PRESSURE:
                menuInflater.inflate(R.menu.menu_blood_pressure_toolbar, menu);
                break;
            case WEIGHT:
                menuInflater.inflate(R.menu.menu_weight_toolbar, menu);
                break;
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String value = null;
        switch (item.getItemId()) {
            case R.id.deleteGlucoseButton:
            case R.id.deleteWeightButton:
                List<SingleValue> selectedItems = singleValueAdapter.getSelectedItems();

                if (selectedItems.isEmpty()) {
                    Toast.makeText(getContext(), "Select the items to delete", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    for (SingleValue singleValue : selectedItems) {
                        singleValueAdapter.removeItem(singleValue);
                        singleValueDAO.deleteSingleValue(singleValue);
                    }
                    //TODO: Notify number of deleted rows
                    return true;
                }
            case R.id.deleteBloodPressureButton:
                value = "blood pressure";
                break;
        }
        Toast.makeText(getContext(), "Button pressed from " + value + " context", Toast.LENGTH_LONG).show();
        return true;
    }

    public SingleValueAdapter getSingleValueAdapter() {
        return singleValueAdapter;
    }

    public DoubleValueAdapter getDoubleValueAdapter() {
        return doubleValueAdapter;
    }
}
