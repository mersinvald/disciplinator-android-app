package me.mersinvald.disciplinator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DayLogListViewAdapter extends ArrayAdapter<HourLog> {
    private final Context context;

    DayLogListViewAdapter(Context context) {
        super(context, R.layout.daylogitem);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HourLog item = this.getItem(position);
        
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View rowView = inflater.inflate(R.layout.daylogitem, parent, false);

        TextView timeView = rowView.findViewById(R.id.daylogHour);
        TextView statView = rowView.findViewById(R.id.daylogStat);
        ImageView imageView = rowView.findViewById(R.id.daylogIsTrackingActiveIcon);
        timeView.setText(context.getString(R.string.time, item.hour));
        statView.setText(context.getString(R.string.stat, item.activeMinutes, item.debt));

        if (item.isTrackingDisabled) {
            imageView.setImageResource(R.drawable.ic_inactive);
        } else {
            imageView.setImageResource(R.drawable.ic_active);
        }

        return rowView;
    }
}
