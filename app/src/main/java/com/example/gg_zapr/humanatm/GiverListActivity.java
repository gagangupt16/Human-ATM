package com.example.gg_zapr.humanatm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GiverListActivity extends AppCompatActivity {

    private static final String TAG = "GiverListActivity";
    private ListView mGiverList;

    private GetGiverTask getGiverTask;

    private List<Giver> givers = new ArrayList<>();
    private volatile  boolean isStop = false;
    private GiverArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_list);
        adapter = new GiverArrayAdapter(this, givers);

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

        try {
            getGivers(adapter);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    private void getGivers(GiverArrayAdapter adapter) throws ExecutionException, InterruptedException {
        if (getGiverTask!= null){
            return;
        }

        getGiverTask = new GetGiverTask(adapter);
        getGiverTask.execute();
    }

    private class GetGiverTask extends AsyncTask<Void, Void, List<Giver>> {

        private GiverArrayAdapter adapter;
        public  GetGiverTask(GiverArrayAdapter adapter){
            this.adapter = adapter;
        }
        @Override
        protected List<Giver> doInBackground(Void... voids) {
            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Giver testGiver = new Giver();
            testGiver.name = "Hello";
            testGiver.distance = 0.5;
            testGiver.lat = 23D;
            testGiver.lon = 67D;

            List<Giver> givers = new ArrayList<>();
            givers.add(testGiver);
            return givers;
        }
        protected void onPostExecute(List<Giver> result) {
            givers.clear();
            givers.addAll(result);
            adapter.notifyDataSetChanged();
            ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar1);
            bar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            getGiverTask = null;
            //showProgress(false);
        }
    }
}
