package com.example.gg_zapr.humanatm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class GiverListActivity extends AppCompatActivity {

    private ListView mGiverList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_list);

        Giver testGiver = new Giver();
        testGiver.name = "Hello";
        testGiver.distance = 0.5;

        List<Giver> givers = Arrays.asList(testGiver);

        GiverArrayAdapter adapter = new GiverArrayAdapter(this, givers);
        mGiverList = (ListView) findViewById(R.id.giverListView);
        mGiverList.setAdapter(adapter);

        mGiverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


}
