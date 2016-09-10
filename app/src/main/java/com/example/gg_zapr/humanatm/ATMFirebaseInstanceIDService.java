package com.example.gg_zapr.humanatm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gg-zapr on 10/9/16.
 */
public class ATMFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "ATMFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        System.out.print("***********************************************************");

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.i(TAG, "Refreshed token: " + refreshedToken);

        SharedPreferences sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.GCM_ID, refreshedToken);
        editor.apply();
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
}
