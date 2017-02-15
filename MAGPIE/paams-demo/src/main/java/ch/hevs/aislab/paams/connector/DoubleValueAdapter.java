package ch.hevs.aislab.paams.connector;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.hevs.aislab.paams.model.DoubleValue;
import ch.hevs.aislab.paamsdemo.R;

public class DoubleValueAdapter extends BaseAdapter {

    private List<DoubleValue> items = new ArrayList<>();

    private final Context context;

    public DoubleValueAdapter(Context context, List<DoubleValue> items) {
        this.context = context;
        this.items = items;
    }

    public void addItem(DoubleValue item) {
        items.add(item);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.double_value_row, parent, false);
        }

        DoubleValue doubleValue = items.get(position);
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoubleValue doubleValue = items.get(position);
                ImageView checkImageView = (ImageView) view.findViewById(R.id.checkImageView);
                if (doubleValue.isMarked()) {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_off);
                    doubleValue.setMarked(false);
                } else {
                    checkImageView.setImageResource(R.mipmap.btn_check_buttonless_on);
                    doubleValue.setMarked(true);
                }
            }
        });
        return convertView;
    }
}
