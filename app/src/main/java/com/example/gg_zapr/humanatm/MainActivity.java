package com.example.gg_zapr.humanatm;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.gg_zapr.humanatm.Constants.GCM_ID;

public class MainActivity extends AppCompatActivity {

    private static final String GIVER_TOPIC = "giver";
    private static final String REQUEST_TOPIC = "request";
    private static final String TAG = "MainActivity";

    private EditText mAmountView;
    private RequestTask mRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAmountView = (EditText) findViewById(R.id.amount);

        Button requestButton = (Button) findViewById(R.id.request_button);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAmount();
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic(GIVER_TOPIC);
        FirebaseMessaging.getInstance().subscribeToTopic(REQUEST_TOPIC);
    }

    private void requestAmount() {

        if (mRequestTask!=null){
            return;
        }

        String amount = mAmountView.getText().toString();
        System.out.println(amount);

        mRequestTask = new RequestTask(Float.parseFloat(amount), this);
        mRequestTask.execute((Void)null);
    }

    private class RequestTask extends AsyncTask<Void, Void, Boolean> {

        private final Float mAmount;
        private final Context mContext;

        RequestTask(Float amount, Context context){
            mAmount = amount;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SharedPreferences sharedPref = mContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String userId = sharedPref.getString(Constants.USER_ID, null);

            Log.i(TAG, "Retrieved UserID: " + userId);

            HttpURLConnection connection;
            URL url;

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userId", userId);
                jsonObject.accumulate("amount", mAmount);
                jsonObject.accumulate("lat", 12.971599);
                jsonObject.accumulate("lon", 77.594563);
                jsonObject.accumulate("distanceMeters", 500);

                String json = jsonObject.toString();

                Log.i(TAG, json);

                url = new URL(Constants.BASE_URL + "requestPayment");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.e(TAG, "Payment request successfully made");
                    return true;
                } else {
                    Log.e(TAG, "Payment request failed");
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRequestTask = null;
            System.out.print("Request successful");
            Intent intent = new Intent(getApplicationContext(),GiverListActivity.class);
            intent.putExtra("amount",mAmount);
            startActivity(intent);

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
            mRequestTask = null;
            System.out.print("Request failed");
            //showProgress(false);
        }
    }
}
