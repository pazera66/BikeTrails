package com.example.pazera.biketrails;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pazera on 26-08-2015.
 */
public class AdapterMyTrailsList extends ArrayAdapter<Trail> {

    private Activity context;
    private LayoutInflater inflater;
    private List<Trail> trails;
    TextView nameView, distanceView, timeView, speedView, caloriesView;

    public AdapterMyTrailsList(Activity context, List<Trail> trails) {
        super(context, R.layout.trail_list_row_layout, trails);
        this.trails = trails;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = context.getLayoutInflater().inflate(R.layout.trail_list_row_layout, parent, false);
        }
        inflater = LayoutInflater.from(getContext());
        //View theView = inflater.inflate(R.layout.trail_list_row_layout, parent, false);

        nameView = (TextView) convertView.findViewById(R.id.my_trail_name);
        distanceView = (TextView) convertView.findViewById(R.id.myTrailDistance);
        timeView = (TextView) convertView.findViewById(R.id.myTrailTime);
        speedView = (TextView) convertView.findViewById(R.id.myTrailSpeed);
        caloriesView = (TextView) convertView.findViewById(R.id.myTrailCalories);

        Double distance = trails.get(position).getDistance();
        String distanceS = distance.toString();

        int seconds = trails.get(position).getTime();
        int mseconds = seconds % 60;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        String timeTemplate = String.format("%02d", hours) + ":"
                + String.format("%02d", minutes) + ":" + String.format("%02d", mseconds);

        Double speedTemplate = trails.get(position).getAvgSpeed();
        double caloriesTemplate = trails.get(position).getCalories();

        nameView.setText(trails.get(position).getName());
        distanceView.setText(context.getString(R.string.distanceSpace) + distanceS + " km");
        timeView.setText(context.getString(R.string.timeSpace) + timeTemplate);
        speedView.setText(context.getString(R.string.SpeedSpace) + speedTemplate.toString() + " km/h");
        caloriesView.setText(context.getString(R.string.caloriesSpace) + String.format("%.0f", (float) caloriesTemplate));


        return convertView;
    }



}
