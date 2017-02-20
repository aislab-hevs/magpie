package ch.hevs.aislab.paams.connector;


import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.model.Value;

public abstract class ValueAdapter extends BaseAdapter {

    List<Value> items;

    ValueAdapter() {
        this.items = new ArrayList<>();
    }

    ValueAdapter(List<Value> items) {
        this.items = items;
    }

    public void addItem(Value item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addAllItems(List<Value> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(Value item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public Value[] getItems() {
        Value[] items = new Value[this.items.size()];
        for (int i = 0; i < this.items.size(); i++) {
            items[i] = this.items.get(i);
        }
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
