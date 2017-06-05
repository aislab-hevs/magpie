package ch.hevs.aislab.paams.connector;


import android.util.Log;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.hevs.aislab.paams.model.Value;

public abstract class ValueAdapter extends BaseAdapter {

    private static final String TAG = "ValueAdapter";

    List<Value> items;
    List<Value> hiddenItems;

    ValueAdapter() {
        this.items = new ArrayList<>();
        this.hiddenItems = new ArrayList<>();
    }

    ValueAdapter(List<Value> items) {
        this.items = items;
        if (this.hiddenItems == null) {
            this.hiddenItems = new ArrayList<>();
        }
        sortItemsByDate();
    }

    public void addItem(Value item) {
        items.add(item);
        sortItemsByDate();
        notifyDataSetChanged();
    }

    public void addAllItems(List<Value> items) {
        this.items = items;
        sortItemsByDate();
        notifyDataSetChanged();
    }

    public void removeItem(Value item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public void displayDummyData(boolean show) {
        Log.i(TAG, "displayDummyData with " + show);
        if (show) {
            if (!this.hiddenItems.isEmpty()) {
                this.items.addAll(this.hiddenItems);
                this.hiddenItems.clear();
            }
        } else {
            for (Value value : this.items) {
                if (value.isDummy()) {
                    this.hiddenItems.add(value);
                }
            }
            this.items.removeAll(this.hiddenItems);
        }
        sortItemsByDate();
        notifyDataSetChanged();
    }

    public Value[] getItems() {
        Value[] items = new Value[this.items.size()];
        for (int i = 0; i < this.items.size(); i++) {
            items[i] = this.items.get(i);
        }
        sortItemsByDate();
        return items;
    }

    public List<Value> getSelectedItems() {
        List<Value> selectedItems = new ArrayList<>();
        for (Value value : items) {
            if (value.isMarked()) {
                selectedItems.add(value);
            }
        }
        return selectedItems;
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

}
