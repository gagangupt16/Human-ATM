package com.example.gg_zapr.humanatm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gg-zapr on 10/9/16.
 */
public class GiverArrayAdapter extends ArrayAdapter<Giver> {
    private final Context context;
    private final List<Giver> values;

//    public GiverArrayAdapter(Context context, [] values) {
//        super(context, R.layout.activity_giver_single, values);
//        this.context = context;
//        this.values = values;
//    }

    public GiverArrayAdapter(Context context, List<Giver> values) {
        super(context, R.layout.activity_giver_single, values);
        this.context = context;
        this.values = values;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_giver_single, parent, false);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        TextView distView = (TextView) rowView.findViewById(R.id.distance);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        // Change the icon for Windows and iPhone
//        String s = values[position];
//        if (s.startsWith("Windows7") || s.startsWith("iPhone")
//                || s.startsWith("Solaris")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }

        nameView.setText(values.get(position).name);
        distView.setText(values.get(position).distance.toString());

        return rowView;
    }
}
