package ch.hevs.aislab.paams.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.hevs.aislab.paams.model.Type;
import ch.hevs.aislab.paamsdemo.R;

public class RuleFragment extends Fragment {

    private static final String ARG_TYPE = "TYPE_OF_PHYSIOLOGICAL_VALUE";

    private Type type;

    public static RuleFragment newInstance(Type type) {
        RuleFragment fragment = new RuleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type.name());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = Type.valueOf(getArguments().getString(ARG_TYPE));
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rule, container, false);

        TextView ruleEnglishTextView = (TextView) view.findViewById(R.id.ruleEnglishTextView);
        TextView ruleECTextView = (TextView) view.findViewById(R.id.ruleECTextView);
        switch (type) {
            case GLUCOSE:
                ruleEnglishTextView.setText(R.string.glucose_rule_english);
                ruleECTextView.setText(R.string.glucose_rule_ec);
                break;
            case BLOOD_PRESSURE:
                ruleEnglishTextView.setText(R.string.blood_pressure_rule_english);
                ruleECTextView.setText(R.string.blood_pressure_rule_ec);
                break;
            case WEIGHT:
                ruleEnglishTextView.setText(R.string.weight_rule_english);
                ruleECTextView.setText(R.string.weight_rule_ec);
                break;
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
