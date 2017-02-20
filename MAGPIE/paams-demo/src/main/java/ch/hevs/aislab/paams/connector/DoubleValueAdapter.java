package ch.hevs.aislab.paams.connector;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paams.model.Value;
import ch.hevs.aislab.paamsdemo.R;

public class DoubleValueAdapter extends ValueAdapter {

    public DoubleValueAdapter() {
        super();
    }

    public DoubleValueAdapter(List<Value> items) {
        super(items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.double_value_row, parent, false);
        }

        final DoubleValue doubleValue = (DoubleValue) items.get(position);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        dateTextView.setText(doubleValue.getStringTimestamp("dd.MM.yyyy"));
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
        timeTextView.setText(doubleValue.getStringTimestamp("HH:mm"));
        TextView firstValueTextView = (TextView) convertView.findViewById(R.id.firstValueTextView);
        firstValueTextView.setText(String.valueOf(doubleValue.getFirstValue()));
        TextView secondValueTextView = (TextView) convertView.findViewById(R.id.secondValueTextView);
        secondValueTextView.setText(String.valueOf(doubleValue.getSecondValue()));
        ImageView checkImageView = (ImageView) convertView.findViewById(R.id.checkImageView);
        checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);

        return convertView;
    }
}
