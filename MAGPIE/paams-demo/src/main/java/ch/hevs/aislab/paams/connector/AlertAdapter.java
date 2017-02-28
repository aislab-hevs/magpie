package ch.hevs.aislab.paams.connector;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.model.Alert;

public class AlertAdapter extends BaseAdapter {

    List<Alert> items;

    public AlertAdapter() {
        this.items = new ArrayList<>();
    }

    public AlertAdapter(List<Alert> items) {
        this.items = items;
    }

    public void addAllItems(List<Alert> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
