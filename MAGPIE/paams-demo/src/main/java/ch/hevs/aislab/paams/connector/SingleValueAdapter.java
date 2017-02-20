package ch.hevs.aislab.paams.connector;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.hevs.aislab.paams.model.SingleValue;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class SingleValueAdapter extends ValueAdapter {

    public SingleValueAdapter() {
        super();
    }

    public SingleValueAdapter(List<Value> items) {
        super(items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_value_row, parent, false);
        }

        // Populate the UI items with the values of the Object
        final SingleValue singleValue = (SingleValue) items.get(position);

        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        dateTextView.setText(singleValue.getStringTimestamp("dd.MM.yyyy"));
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        timeTextView.setText(singleValue.getStringTimestamp("HH:mm"));
        TextView valueTextView = (TextView) convertView.findViewById(R.id.valueTextView);
        valueTextView.setText(String.valueOf(singleValue.getValue()));
        ImageView checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
        checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
        return convertView;
    }
}
