package com.example.gg_zapr.humanatm;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String GIVER_TOPIC = "giver";
    private static final String REQUEST_TOPIC = "request";

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

        mRequestTask = new RequestTask(Float.parseFloat(amount));
        mRequestTask.execute((Void)null);
    }

    private class RequestTask extends AsyncTask<Void, Void, Boolean> {

        private final Float mAmount;

        RequestTask(Float amount){
            mAmount = amount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRequestTask = null;
            System.out.print("Request successful");
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
