package ch.hevs.aislab.magpie.debs.background;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.magpie.debs.R;
import ch.hevs.aislab.magpie.debs.model.GlucoseAlert;

public class AlertsListAdapter extends BaseAdapter{

    private List<GlucoseAlert> alerts = new ArrayList<>();
    private Context mContext;

    public AlertsListAdapter(Context context) {
        this.mContext = context;
    }

    public void addItem(GlucoseAlert alert) {
        alerts.add(alert);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public GlucoseAlert getItem(int pos) {
        return alerts.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alert_item, parent, false);
        }

        TextView typeTxtView = (TextView) convertView.findViewById(R.id.typeTxtView);
        TextView dateTxtView = (TextView) convertView.findViewById(R.id.dateTxtView);

        GlucoseAlert alert = alerts.get(position);

        typeTxtView.setText(alert.getType().toString().toLowerCase());
        dateTxtView.setText(alert.getDate());

        return convertView;
    }
}
