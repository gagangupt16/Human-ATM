package com.example.gg_zapr.humanatm;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiverListActivity extends AppCompatActivity {

    private ListView mGiverList;

    private GetGiverTask getGiverTask;

    private List<Giver> givers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giver_list);

        GiverArrayAdapter adapter = new GiverArrayAdapter(this, givers);
        mGiverList = (ListView) findViewById(R.id.giverListView);
        mGiverList.setAdapter(adapter);

        mGiverList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                Toast.makeText(getApplicationContext(),
                        "Giver " + position, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), GiverDetailActivity.class);
                intent.putExtra("giver", givers.get(position));
                startActivity(intent);
            }
        });

        getGivers();
    }

    private void getGivers() {
        if (getGiverTask!= null){
            return;
        }

        getGiverTask = new GetGiverTask();
        getGiverTask.execute();
    }

    private class GetGiverTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            Giver testGiver = new Giver();
            testGiver.name = "Hello";
            testGiver.distance = 0.5;
            testGiver.lat = 23D;
            testGiver.lon = 67D;

            givers.add(testGiver);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getGiverTask = null;
//            showProgress(false);
//
//            if (success) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
        }

        @Override
        protected void onCancelled() {
            getGiverTask = null;
            //showProgress(false);
        }
    }
}
