package ch.hevs.aislab.paams.connector;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.hevs.aislab.paams.model.Alert;
import ch.hevs.aislab.paamsdemo.R;

public class AlertAdapter extends BaseAdapter {

    private static final String TAG = "AlertAdapter";

    private Context context;
    private List<Alert> items;
    private List<Alert> hiddenItems;

    public AlertAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        this.hiddenItems = new ArrayList<>();
    }

    public AlertAdapter(Context context, List<Alert> items) {
        this.context = context;
        this.items = items;
        if (this.hiddenItems == null) {
            this.hiddenItems = new ArrayList<>();
        }
        sortItemsByDate();
    }

    public void addAllItems(List<Alert> items) {
        this.items = items;
        sortItemsByDate();
        notifyDataSetChanged();
    }

    public void removeItem(Alert item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public Alert[] getItems() {
        Alert[] items = new Alert[this.items.size()];
        for (int i = 0; i < this.items.size(); i++) {
            items[i] = this.items.get(i);
        }
        sortItemsByDate();
        return items;
    }

    public List<Alert> getSelectedItems() {
        List<Alert> selectedItems = new ArrayList<>();
        for (Alert alert : items) {
            if (alert.isMarked()) {
                selectedItems.add(alert);
            }
        }
        return selectedItems;
    }

    public void displayDummyData(boolean show) {
        Log.i(TAG, "displayDummyData with " + show);
        if (show) {
            if (!this.hiddenItems.isEmpty()) {
                this.items.addAll(this.hiddenItems);
                this.hiddenItems.clear();
            }
        } else {
            for (Alert alert : this.items) {
                if (alert.isDummy()) {
                    this.hiddenItems.add(alert);
                }
            }
            this.items.removeAll(this.hiddenItems);
        }
        sortItemsByDate();
        notifyDataSetChanged();
    }

    private void sortItemsByDate() {
        Collections.sort(this.items);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.alert_row, parent, false);
        }

        final Alert alert = items.get(position);

        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateAlertTextView);
        dateTextView.setText(alert.getStringTimestamp("dd.MM.yyyy"));
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeAlertTextView);
        timeTextView.setText(alert.getStringTimestamp("H:mm"));
        TextView valueTextView = (TextView) convertView.findViewById(R.id.nameAlertTextView);
        valueTextView.setText(String.valueOf(alert.getName()));
        ImageView checkImageView = (ImageView) convertView.findViewById(R.id.checkAlertImageView);
        checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);

        return convertView;
    }
}
