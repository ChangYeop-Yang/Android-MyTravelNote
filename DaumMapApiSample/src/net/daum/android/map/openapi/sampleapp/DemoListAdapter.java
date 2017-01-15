package net.daum.android.map.openapi.sampleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DemoListAdapter extends ArrayAdapter<DemoListItem> {

    public DemoListAdapter(Context context, DemoListItem[] demos) {
        super(context, R.layout.demo_list_item_view, demos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DemoListItem demo = getItem(position);


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.demo_list_item_view, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(demo.titleId);

        TextView description = (TextView) convertView.findViewById(R.id.description);
        description.setText(demo.descriptionId);

        return convertView;
    }
}
