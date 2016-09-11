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

import org.json.JSONArray;
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
        final float amount = getIntent().getFloatExtra("amount", 0);

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

        getGiverTask = new GetGiverTask(adapter, this);
        getGiverTask.execute();
    }

    private class GetGiverTask extends AsyncTask<Void, Void, List<Giver>> {

        private GiverArrayAdapter adapter;
        private Context mContext;

        public  GetGiverTask(GiverArrayAdapter adapter, Context context){
            this.adapter = adapter;
            mContext = context;
        }
        @Override
        protected List<Giver> doInBackground(Void... voids) {

//            List<Giver> givers = new ArrayList<>();
//            Giver giver = new Giver();
//            giver.id = "id1";
//            giver.name = "Normal friend";
//            giver.distance = 400d;
//            giver.lat = 12.971599;
//            giver.lon = 77.594563;
//
//            Giver giver2 = new Giver();
//            giver2.id = "id2";
//            giver2.name = "FB friend";
//            giver2.distance = 400d;
//            giver2.lat = 12.971599;
//            giver2.lon = 77.594563;
//            giver2.isFb = true;
//
//            givers.add(giver);
//            givers.add(giver2);

//            return givers;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPref = mContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String userId = sharedPref.getString(Constants.USER_ID, null);

            Log.i(TAG, "Retrieved UserID: " + userId);

            HttpURLConnection connection;
            URL url;

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userId", userId);
                String json = jsonObject.toString();

                Log.i(TAG, json);

                url = new URL(Constants.BASE_URL + "getAllFulfillers");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.e(TAG, "Fulfiller request successfully made");

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    String responseStr = response.toString();
                    Log.i(TAG, responseStr);

                    List<Giver> givers = new ArrayList<>();

                    JSONArray jsonarray = new JSONArray(responseStr);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String id = jsonobject.getString("userId");
                        String name = jsonobject.getString("name");
                        Double distance = jsonobject.getDouble("distance");
                        Double lat = jsonobject.getDouble("lat");
                        Double lon = jsonobject.getDouble("lon");

                        Giver giver = new Giver();
                        giver.id = id;
                        giver.name = name;
                        giver.distance = distance;
                        giver.lat = lat;
                        giver.lon = lon;

                        givers.add(giver);
                    }

                    Giver giver2 = new Giver();
                    giver2.id = "id2";
                    giver2.name = "Rajat Gupta";
                    giver2.distance = 400d;
                    giver2.lat = 12.971599;
                    giver2.lon = 77.594563;
                    giver2.isFb = true;

                    givers.add(giver2);

                    return givers;

                } else {
                    Log.e(TAG, "Fulfiller List request failed");
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(List<Giver> result) {

            if (result!= null){
                givers.clear();
                givers.addAll(result);
            }

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
