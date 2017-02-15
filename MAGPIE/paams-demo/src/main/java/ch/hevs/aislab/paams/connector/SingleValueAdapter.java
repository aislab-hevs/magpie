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
import java.util.List;

import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paamsdemo.R;

public class SingleValueAdapter extends BaseAdapter {

    private final Context context;
    private List<SingleValue> items;


    public SingleValueAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
    }

    public SingleValueAdapter(Context context, List<SingleValue> items) {
        this.context = context;
        this.items = items;
    }

    public void addItem(SingleValue item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addAllItems(List<SingleValue> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(SingleValue item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    public SingleValue[] getItems() {
        SingleValue[] items = new SingleValue[this.items.size()];
        for (int i = 0; i < this.items.size(); i++) {
            items[i] = this.items.get(i);
        }
        return items;
    }

    public List<SingleValue> getSelectedItems() {
        List<SingleValue> selectedItems = new ArrayList<>();

        /***** DEBUG *****/
        Log.i("SingleValueAdapter", "Number of items in the list: " + items.size());
        /***** DEBUG *****/

        for (SingleValue singleValue : items) {
            Log.i("SingleValueAdapter", "SingleValue " + singleValue.getId() + " is " + singleValue.toString() + " in getSelectedItems()");
            if (singleValue.isMarked()) {
                selectedItems.add(singleValue);
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_value_row, parent, false);
        }

        // Populate the UI items with the values of the Object
        final SingleValue singleValue = items.get(position);

        Log.i("SingleValueAdapter", "The list is " + items.toString() + " in getView()");
        Log.i("SingleValueAdapter", "SingleValue " + singleValue.getId() + " is " + singleValue.toString() + " in getView()");

        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        dateTextView.setText(singleValue.getStringTimestamp("dd.MM.yyyy"));
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        timeTextView.setText(singleValue.getStringTimestamp("HH:mm"));
        TextView valueTextView = (TextView) convertView.findViewById(R.id.valueTextView);
        valueTextView.setText(String.valueOf(singleValue.getValue()));
        ImageView checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
        checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);

        //convertView.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
                //ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
                //if (singleValue.isMarked()) {
                    //checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
                    //singleValue.setMarked(false);
                //} else {
                    //checkImageView.setImageResource(R.mipmap.btn_check_buttonless_on);
                    //singleValue.setMarked(true);
                //}
            //}
        //});
        return convertView;
    }
}
