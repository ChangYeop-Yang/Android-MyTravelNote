package net.daum.android.map.openapi.sampleapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListAdapter adapter = new DemoListAdapter(this, DemoListItem.DEMO_LIST_ITEMS);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DemoListItem demo = (DemoListItem) getListAdapter().getItem(position);
        startActivity(new Intent(this, demo.activity));
    }

}
