package com.example.gg_zapr.humanatm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GiverListActivity extends AppCompatActivity {

    private ListView mGiverList;

    private GetGiverTask getGiverTask;

    private List<Giver> givers = new ArrayList<>();
    private volatile  boolean isStop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_list);

        GiverArrayAdapter adapter = new GiverArrayAdapter(this, givers);
        mGiverList = (ListView) findViewById(R.id.giverListView);
        mGiverList.setAdapter(adapter);
        final float amount = getIntent().getFloatExtra("amount",0);

        mGiverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Toast.makeText(getApplicationContext(),
                        "Giver " + position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), GiverDetailActivity.class);
                intent.putExtra("giver", givers.get(position));
                intent.putExtra("amount", amount);
                startActivity(intent);
            }
        });
//        while(!isStop) {
//
//            try {
//                Thread.sleep(2000);
//                getGivers();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        try {
            getGivers();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getGivers() throws ExecutionException, InterruptedException {
        if (getGiverTask!= null){
            return;
        }

        getGiverTask = new GetGiverTask();

        List<Giver> result = getGiverTask.execute().get();
//        if(result.size()>0){
//            ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar1);
//            bar.setVisibility(View.INVISIBLE);
//        }
        givers.clear();
        givers.addAll(result);

    }

    private class GetGiverTask extends AsyncTask<Void, Void, List<Giver>> {
        @Override
        protected List<Giver> doInBackground(Void... voids) {
            Giver testGiver = new Giver();
            testGiver.name = "Hello";
            testGiver.distance = 0.5;
            testGiver.lat = 23D;
            testGiver.lon = 67D;

            List<Giver> givers = new ArrayList<>();
            givers.add(testGiver);
            return givers;
        }


        @Override
        protected void onCancelled() {
            getGiverTask = null;
            //showProgress(false);
        }
    }
}
