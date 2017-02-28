package ch.hevs.aislab.paams.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.connector.AlertAdapter;
import ch.hevs.aislab.paams.connector.AlertDAO;
import ch.hevs.aislab.paams.model.Alert;
import ch.hevs.aislab.paamsdemo.R;


public class AlertFragment extends ListFragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
