package com.newtonapps.hangtight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, String[] workoutData) {
        super(context,R.layout.loadscreen_list_view_elements, workoutData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.loadscreen_list_view_elements, parent, false);

        TextView title = (TextView) customView.findViewById(R.id.title);
        TextView description = (TextView) customView.findViewById(R.id.description);
        TextView extras = (TextView) customView.findViewById(R.id.extrasTV);

        String dataString = getItem(position);
        String[] splitData = dataString.split("\\|");


        String extraInfo = splitData[2];
        for(int i=3; i<splitData.length; i++){
            extraInfo += "-" + splitData[i];
        }

        title.setText(splitData[0]);
        description.setText(splitData[1]);
        extras.setText(extraInfo);

        return customView;
    }
}
