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

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.connector.AlertAdapter;
import ch.hevs.aislab.paams.connector.AlertDAO;
import ch.hevs.aislab.paams.model.Alert;
import ch.hevs.aislab.paamsdemo.R;


public class AlertFragment extends ListFragment {

    private final String TAG = getClass().getName();

    private static final String BUNDLE_KEY = "alerts";

    private AlertAdapter alertAdapter;
    private AlertDAO alertDAO;

    public AlertFragment() {
        // Required empty public constructor
    }

    public static AlertFragment newInstance() {
        return new AlertFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            alertAdapter = new AlertAdapter(getActivity());
        } else {
            Alert[] alerts = (Alert[]) savedInstanceState.getParcelableArray(BUNDLE_KEY);
            List<Alert> alertsList = new ArrayList<>();
            for (Alert alert : alerts) {
                alertsList.add(alert);
            }
            alertAdapter = new AlertAdapter(getActivity(), alertsList);
        }

        alertDAO = new AlertDAO(getActivity());
        alertDAO.open();
        if (alertAdapter.getCount() == 0) {
            alertAdapter.addAllItems(alertDAO.getAllAlerts());
        }
        setListAdapter(alertAdapter);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setHeaderDividersEnabled(true);
        LinearLayout headerLayout = (LinearLayout) LayoutInflater
                .from(getActivity()).inflate(R.layout.alert_row_header, getListView(), false);

        getListView().addHeaderView(headerLayout, null, false);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Alert alert = (Alert) alertAdapter.getItem(--position);
                ImageView checkImageView = (ImageView) view.findViewById(R.id.checkAlertImageView);
                if (alert.isMarked()) {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
                    alert.setMarked(false);
                } else {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_on);
                    alert.setMarked(true);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        alertDAO.close();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Alert[] items = alertAdapter.getItems();
        if (items.length > 0) {
            outState.putParcelableArray(BUNDLE_KEY, items);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Log.i(TAG, "WE ENTERED HERE!");
        menu.clear();
        menuInflater.inflate(R.menu.menu_delete_value_toolbar, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected()");
        return false;
    }
}
