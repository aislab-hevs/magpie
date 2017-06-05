package ch.hevs.aislab.paams.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.hevs.aislab.paams.connector.ValueAdapter;
import ch.hevs.aislab.paams.connector.ValueDAO;
import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendsFragment extends Fragment {

    private static final String TAG = "TrendsFragment";

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";
    private static final String BUNDLE_KEY = "items";

    private Type type;
    private static ValueAdapter valueAdapter;
    private ValueDAO valueDAO;

    public TrendsFragment() {

    }

    public static TrendsFragment newInstance(Type type) {
        TrendsFragment fragment = new TrendsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trends, container, false);
    }

    @Override
    public void onDestroy() {
        //TODO: Save the marked state
        valueDAO.close();
        super.onDestroy();
    }
}
