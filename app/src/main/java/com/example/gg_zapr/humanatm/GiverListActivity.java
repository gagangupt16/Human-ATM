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

        getGiverTask = new GetGiverTask(this);

        List<Giver> result = getGiverTask.execute().get();
//        if(result.size()>0){
//            ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar1);
//            bar.setVisibility(View.INVISIBLE);
//        }

        if (result != null){
            givers.clear();
            givers.addAll(result);
        }
    }

    private class GetGiverTask extends AsyncTask<Void, Void, List<Giver>> {

        private final Context mContext;

        public GetGiverTask(Context context) {
            mContext = context;
        }

        @Override
        protected List<Giver> doInBackground(Void... voids) {
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
                    Log.i(TAG, "Givers list request successfully made");

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }

                    reader.close();
                    String responseStr = response.toString();

                    JSONObject responseObject = new JSONObject(responseStr);

                    List<Giver> givers = new ArrayList<>();

                    return givers;

                } else {
                    Log.e(TAG, "Giver's request failed");
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            getGiverTask = null;
            //showProgress(false);
        }
    }
}
