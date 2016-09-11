package com.example.gg_zapr.humanatm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by gg-zapr on 10/9/16.
 */
public class ATMFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ATMFirebaseMsgService";
    private static final String ACTION_HELP = "action_help";

    private static String requestId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Calling method to generate notification
        if (remoteMessage.getData()!= null){
            sendNotification(remoteMessage.getData());
        }
    }

    private void sendNotification(Map<String, String> data) {
        try {
            //Intent intent = null;
            NotificationCompat.Builder notificationBuilder = null;
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent = new Intent(this, NotificationActionService.class)
                    .setAction(ACTION_HELP);

//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(GiverListActivity.class);
            stackBuilder.addNextIntent(intent);

            if (data.containsKey("type")){
                notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.transferred))
                        .setContentText(String.format("%.2f is transferred to your account.",
                                Float.parseFloat(data.get("amount").toString())))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);
            }
            else {
                requestId = data.get("requestId");
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.need_help))
                        .setContentText(String.format("%s needs %.2f in cash. Can you help?", data.get("name").toString(),
                                Float.parseFloat(data.get("amount").toString())))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .addAction(R.mipmap.app_bar_next, "Help", pendingIntent);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static class NotificationActionService extends IntentService {

        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();

            Log.i(TAG, "Received notification action: " + action);

            if (ACTION_HELP.equals(action)) {
                HttpURLConnection connection;
                URL url;

                try{
                    SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    String userId = sharedPref.getString(Constants.USER_ID, null);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("userId", Integer.parseInt(userId));
                    jsonObject.accumulate("paymentId", Integer.parseInt(requestId));

                    String json = jsonObject.toString();

                    Log.i(TAG, json);

                    url = new URL(Constants.BASE_URL + "agreePayment");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");

                    DataOutputStream wr = new DataOutputStream(
                            connection.getOutputStream());
                    wr.writeBytes(json);
                    wr.flush();
                    wr.close();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        Log.e(TAG, "help request successfully made");
                    } else {
                        Log.e(TAG, "help request failed");
                    }

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.cancel(0);

                    Handler mHandler = new Handler(getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "We will let the person know you are ready. :)", Toast.LENGTH_LONG).show();
                        }
                    });



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}