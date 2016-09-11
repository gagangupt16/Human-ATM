package com.example.gg_zapr.humanatm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.payu.india.CallBackHandler.OnetapCallback;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Interfaces.OneClickPaymentListener;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static com.example.gg_zapr.humanatm.Constants.GCM_ID;


public class PaymentMainActivity extends AppCompatActivity implements View.OnClickListener, OneClickPaymentListener {

    int merchantIndex = 0;

//    int env = PayuConstants.MOBILE_STAGING_ENV;
    // in case of production make sure that merchantIndex is fixed as 0 (0MQaQP) for other key's payu server cant generate hash
  //  int env = PayuConstants.PRODUCTION_ENV;
    int env = PayuConstants.STAGING_ENV;

    String merchantTestKeys[] = {"gtKFFx", "DXOF8m","obScKz", "smsplus"};
//    String merchantTestSalts[] = {"eCwWELxi","2Hl5U0En", "Ml7XBCdR", "350" };

    Boolean smsPermission = true;

    String merchantProductionKeys[] = {"0MQaQP", "smsplus"};
    String merchantProductionSalts[] = {"13p0PXZk", "1b1b0"};

    String offerKeys[] = {"test123@6622", "offer_test@ffer_t5172", "offerfranklin@6636"};

    String merchantKey = env == PayuConstants.PRODUCTION_ENV ? merchantProductionKeys[merchantIndex]:merchantTestKeys[merchantIndex];
    //    String merchantSalt = env == PayuConstants.PRODUCTION_ENV ? merchantProductionSalts[merchantIndex] : merchantTestSalts[merchantIndex];
    String mandatoryKeys[] = { PayuConstants.KEY, PayuConstants.AMOUNT, PayuConstants.PRODUCT_INFO, PayuConstants.FIRST_NAME, PayuConstants.EMAIL, PayuConstants.TXNID, PayuConstants.SURL, PayuConstants.FURL, PayuConstants.USER_CREDENTIALS, PayuConstants.UDF1, PayuConstants.UDF2, PayuConstants.UDF3, PayuConstants.UDF4, PayuConstants.UDF5, PayuConstants.ENV, PayuConstants.STORE_ONE_CLICK_HASH, PayuConstants.SMS_PERMISSION};
    String mandatoryValues[] = { merchantKey, "10.0", "myproduct", "firstname", "me@itsme.com", ""+System.currentTimeMillis(), "https://payu.herokuapp.com/success", "https://payu.herokuapp.com/failure", merchantKey+":payutest@payu.in", "udf1", "udf2", "udf3", "udf4", "udf5", ""+env, ""+ PayuConstants.STORE_ONE_CLICK_HASH_SERVER, smsPermission.toString() };

    int idsKey[] = {R.id.k_merchant_key, R.id.k_amount, R.id.k_product_info, R.id.k_first_name, R.id.k_email, R.id.k_txnid, R.id.k_surl, R.id.k_furl, R.id.k_user_credentials, R.id.k_udf1, R.id.k_udf2, R.id.k_udf3, R.id.k_udf4, R.id.k_udf5, R.id.k_env, R.id.k_store_one_click_payment, R.id.k_sms_permission };
    int idsValue[] = {R.id.v_merchant_key, R.id.v_amount, R.id.v_product_info, R.id.v_first_name, R.id.v_email, R.id.v_txnid, R.id.v_surl, R.id.v_furl, R.id.v_user_credentials, R.id.v_udf1, R.id.v_udf2, R.id.v_udf3, R.id.v_udf4, R.id.v_udf5, R.id.v_env, R.id.v_store_one_click_payment, R.id.v_sms_permission };


    String inputData = "";

    private Toolbar toolBar;
    private Button addButton;
    private Button nextButton;
    private ScrollView mainScrollView;
    private LinearLayout rowContainerLinearLayout;

    private PayUChecksum checksum;
    private PostData postData;
    private String key;
    private String salt;
    private String var1;
    private Intent intent;
    //    private mPaymentParams mPaymentParams;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private String cardBin;
    private int storeOneClickHash;
    Bundle bundle;
    EditText leftChild;
    EditText rightChild;
    String giverId;
    float amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        OnetapCallback.setOneTapCallback(this);

        Payu.setInstance(this);
        bundle = getIntent().getExtras();
        amount = getIntent().getFloatExtra("amount", 10);
        giverId = getIntent().getStringExtra("giverId");

        // lets set up the tool bar;
//        toolBar = (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(toolBar);
//
//        // lets initialize the views
//        addButton = (Button) findViewById(R.id.button_add);
//        nextButton = (Button) findViewById(R.id.button_next);
//        rowContainerLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_row_container);
//
//        mainScrollView = (ScrollView) findViewById(R.id.scroll_view_main);

        // lets set the on click listener to the buttons
//        addButton.setOnClickListener(this);
//        nextButton.setOnClickListener(this);


        // filling up the ui with the values.
//        for(int i = 0 ; i < mandatoryKeys.length; i++){
//            addView();
//            LinearLayout currentLayout = (LinearLayout) rowContainerLinearLayout.getChildAt(i);
//            leftChild = ((EditText) currentLayout.getChildAt(0));
//            rightChild = ((EditText)currentLayout.getChildAt(1));
//            leftChild.setText(mandatoryKeys[i]);
//            if(null != mandatoryValues[i])
//                rightChild.setText(mandatoryValues[i]);
//
//            if(i <= mandatoryKeys.length){
//                leftChild.setId(idsKey[i]);
//                rightChild.setId(idsValue[i]);
//            }
//        }

        // lets tell the people what version of sdk we are using
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();

//        Toast.makeText(this, "Build No: " + payUSdkDetails.getSdkBuildNumber() + "\n Build Type: " + payUSdkDetails.getSdkBuildType() + " \n Build Flavor: " +  payUSdkDetails.getSdkFlavor() + "\n Application Id: " + payUSdkDetails.getSdkApplicationId() + "\n Version Code: " + payUSdkDetails.getSdkVersionCode()+ "\n Version Name: " + payUSdkDetails.getSdkVersionName(), Toast.LENGTH_LONG).show();
        navigateToBaseActivity(amount);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        switch (id){
            case R.id.action_exit:
                break;
            case R.id.action_next:
                navigateToBaseActivity(10);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(resultCode==RESULT_OK) {
            if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
                if (data != null) {

                    new AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                            .setMessage("Payment Successful")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();

                } else {
                    Toast.makeText(this, "Could not receive data", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            new AlertDialog.Builder(this)
//                        .setCancelable(false)
//                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                    .setMessage("Payment Failed")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();
        }
        try {
            new SuccessTask(amount,giverId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("payment",true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    class SuccessTask extends AsyncTask<Void, Void, Boolean> {

        private final float amount;
        private final String  fulfillerId;

        public SuccessTask(float amount, String fulfillerId) {
            this.amount = amount;
            this.fulfillerId = fulfillerId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection connection;
            URL url;

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("fulfillerId", Integer.parseInt(fulfillerId));
                jsonObject.accumulate("amount", amount);
                String json = jsonObject.toString();

               Log.i(PaymentMainActivity.class.getName(), json);

                url = new URL(Constants.BASE_URL + "ackTransactionSuccess");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");


                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){

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


                    Log.i(PaymentMainActivity.class.getName(), "Matched: "+ responseStr);
                    return true;
                } else {
                    Log.e(PaymentMainActivity.class.getName(), "Unmatched");
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
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
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case  R.id.button_add:
//                addView();
//                break;
//            case R.id.button_next:
//                navigateToBaseActivity();
//                break;
//        }
    }

    private void addView(){
//        rowContainerLinearLayout.addView(getLayoutInflater().inflate(R.layout.row, null));
//        findViewById(R.id.scroll_view_main).post(new Runnable() {
//            @Override
//            public void run() {
//                mainScrollView.fullScroll(View.FOCUS_DOWN);
//            }
//        });
    }

    private void navigateToBaseActivity(float amount){
        String mandatoryKeys[] = { PayuConstants.KEY, PayuConstants.AMOUNT, PayuConstants.PRODUCT_INFO, PayuConstants.FIRST_NAME, PayuConstants.EMAIL, PayuConstants.TXNID, PayuConstants.SURL, PayuConstants.FURL, PayuConstants.USER_CREDENTIALS, PayuConstants.UDF1, PayuConstants.UDF2, PayuConstants.UDF3, PayuConstants.UDF4, PayuConstants.UDF5, PayuConstants.ENV, PayuConstants.STORE_ONE_CLICK_HASH, PayuConstants.SMS_PERMISSION};
        String mandatoryValues[] = { merchantKey, "10.0", "myproduct", "firstname", "me@itsme.com", ""+System.currentTimeMillis(), Constants.BASE_URL+"dummy", "https://payu.herokuapp.com/failure", merchantKey+":payutest@payu.in", "udf1", "udf2", "udf3", "udf4", "udf5", ""+env, ""+ PayuConstants.STORE_ONE_CLICK_HASH_SERVER, smsPermission.toString() };
        intent = new Intent(this, PayUBaseActivity.class);
//        LinearLayout rowContainerLayout = (LinearLayout) findViewById(R.id.linear_layout_row_container);
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();

        mPaymentParams.setKey(mandatoryValues[0]);
        key=merchantKey;

        if(amount==0) {
            mPaymentParams.setAmount(mandatoryValues[1]);
        }else{
            mPaymentParams.setAmount(amount+"");
        }

        mPaymentParams.setProductInfo(mandatoryValues[2]);

        mPaymentParams.setFirstName(mandatoryValues[3]);

        mPaymentParams.setEmail(mandatoryValues[4]);

        mPaymentParams.setTxnId(mandatoryValues[5]);

        mPaymentParams.setSurl(mandatoryValues[6]);

        mPaymentParams.setFurl(mandatoryValues[7]);

        mPaymentParams.setUserCredentials(mandatoryValues[8]);
            var1=mandatoryValues[8];


        mPaymentParams.setUdf1(mandatoryValues[9]);
        mPaymentParams.setUdf2(mandatoryValues[10]);
        mPaymentParams.setUdf3(mandatoryValues[11]);
        mPaymentParams.setUdf4(mandatoryValues[12]);
        mPaymentParams.setUdf5(mandatoryValues[13]);
        try{
            payuConfig.setEnvironment(Integer.parseInt(mandatoryValues[14]));
        }catch (Exception e){
            payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
        }

        try {
                        storeOneClickHash = Integer.parseInt(mandatoryValues[15]);
                    }catch (Exception e){
                        storeOneClickHash = 0;
                    }

        smsPermission = Boolean.parseBoolean(mandatoryValues[16]);
                    intent.putExtra(PayuConstants.SMS_PERMISSION, smsPermission);




//        int childNodeCount = rowContainerLayout.getChildCount();

//        for(int i = 0; i < childNodeCount; i++){
//            LinearLayout linearLayout = (LinearLayout) rowContainerLayout.getChildAt(i);
//            inputData = ((EditText)linearLayout.getChildAt(1)).getText().toString();
//            switch (((EditText)linearLayout.getChildAt(0)).getText().toString()){
//                case PayuConstants.KEY:
//                    mPaymentParams.setKey(inputData);
//                    key = inputData;
//                    break;
//                case PayuConstants.AMOUNT:
//                    mPaymentParams.setAmount(inputData);
//                    break;
//                case PayuConstants.PRODUCT_INFO:
//                    mPaymentParams.setProductInfo(inputData);
//                    break;
//                case PayuConstants.FIRST_NAME:
//                    mPaymentParams.setFirstName(inputData);
//                    break;
//                case PayuConstants.EMAIL:
//                    mPaymentParams.setEmail(inputData);
//                    break;
//                case PayuConstants.TXNID:
//                    mPaymentParams.setTxnId(inputData);
//                    break;
//                case PayuConstants.SURL:
//                    mPaymentParams.setSurl(inputData);
//                    break;
//                case PayuConstants.FURL:
//                    mPaymentParams.setFurl(inputData);
//                    break;
//                case PayuConstants.UDF1:
//                    mPaymentParams.setUdf1(inputData);
//                    break;
//                case PayuConstants.UDF2:
//                    mPaymentParams.setUdf2(inputData);
//                    break;
//                case PayuConstants.UDF3:
//                    mPaymentParams.setUdf3(inputData);
//                    break;
//                case PayuConstants.UDF4:
//                    mPaymentParams.setUdf4(inputData);
//                    break;
//                case PayuConstants.UDF5:
//                    mPaymentParams.setUdf5(inputData);
//                    break;
//
//                // in case store user card
//
//                case PayuConstants.USER_CREDENTIALS:
//                    mPaymentParams.setUserCredentials(inputData);
//                    var1 = inputData;
//                    break;
//
//                // for offer key
//                case PayuConstants.OFFER_KEY:
//                    mPaymentParams.setOfferKey(inputData);
//                    break;
//
//                // other params- should be inside bundle, so that we can get them in next page.
//                case PayuConstants.SALT:
//                    intent.putExtra(PayuConstants.SALT, inputData);
//                    salt = inputData;
//                    break;
//
//                // stetting up the environment
//                case PayuConstants.ENV:
//                    String environment = inputData;
//                    try{
//                        payuConfig.setEnvironment(Integer.parseInt(inputData));
//                    }catch (Exception e){
//                        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
//                    }
//                    break;
//
//                // is_Domestic
//                case "card_bin":
//                    cardBin = inputData;
//                    break;
//
//                case PayuConstants.STORE_ONE_CLICK_HASH:
//                    try {
//                        storeOneClickHash = Integer.parseInt(inputData);
//                    }catch (Exception e){
//                        storeOneClickHash = 0;
//                    }
//                    break;
//
//                case PayuConstants.SMS_PERMISSION:
//                    smsPermission = Boolean.parseBoolean(inputData);
//                    intent.putExtra(PayuConstants.SMS_PERMISSION, smsPermission);
//
//                /*
//                * if you have any other payment default param please add them here. something like
//                *
//                * case PayuConstants.PHONE:
//                * mPaymentParams.setPhone(((EditText) linearLayout.getChildAt(1)).getText().toString());
//                * break;
//                *
//                * */
//
//            }
//
//        }

        // generate hash from server
        // just a sample. Acturally Merchant should generate from his server.
//        salt = "eCwWELxi";
        if(null == salt) generateHashFromServer(mPaymentParams);
        else {
            generateHashFromSDK(mPaymentParams, intent.getStringExtra(PayuConstants.SALT));
        }

        // generate hash from client;
        /**
         *  just for testing, dont use this in production.
         *  merchant should generate the hash from his server.
         *
         */
//        generateHashFromSDK(mPaymentParams, intent.getStringExtra(PayuConstants.SALT));
    }
    /****************************** Server hash generation ********************************/
    // lets generate hashes from server
    public void generateHashFromServer(PaymentParams mPaymentParams){
//        nextButton.setEnabled(false); // lets not allow the user to click the button again and again.
        // lets create the post params

        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if(null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));
        // for check_isDomestic
        if(null != cardBin)
            postParamsBuffer.append(concatParams("card_bin", cardBin));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        // make api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }


    class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes>{

        @Override
        protected PayuHashes doInBackground(String ... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {
//                URL url = new URL(PayuConstants.MOBILE_TEST_FETCH_DATA_URL);
//                        URL url = new URL("http://10.100.81.49:80/merchant/postservice?form=2");;

                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while(payuHashIterator.hasNext()){
                    String key = payuHashIterator.next();
                    switch (key){
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        case "get_merchant_ibibo_codes_hash": //
                            payuHashes.setMerchantIbiboCodesHash(response.getString(key));
                            break;
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        case "check_isDomestic_hash":
                            payuHashes.setCheckIsDomesticHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);
//            nextButton.setEnabled(true);
            launchSdkUI(payuHashes);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        LinearLayout rowContainerLayout = (LinearLayout) findViewById(R.id.linear_layout_row_container);
//
//        int childNodeCount = rowContainerLayout.getChildCount();
//        // we need a unique txnid every time..
//        for(int i = 0; i < childNodeCount; i++){
//            LinearLayout linearLayout = (LinearLayout) rowContainerLayout.getChildAt(i);
//            switch (((EditText)linearLayout.getChildAt(0)).getText().toString()){
//                case PayuConstants.TXNID: // lets set up txnid.
//                    ((EditText) linearLayout.getChildAt(1)).setText(""+System.currentTimeMillis());
//                    break;
//            }
//
//        }
    }


    public void launchSdkUI(PayuHashes payuHashes){
        // let me add the other params which i might use from other activity

        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
//        intent.putExtra(PayuConstants.PAYMENT_DEFAULT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

        /**
         *  just for testing, dont do this in production.
         *  i need to generate hash for {@link com.payu.india.Tasks.GetTransactionInfoTask} since it requires transaction id, i don't generate hash from my server
         *  merchant should generate the hash from his server.
         *
         */
        intent.putExtra(PayuConstants.SALT, salt);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH, storeOneClickHash);

        if(storeOneClickHash == PayuConstants.STORE_ONE_CLICK_HASH_SERVER)
            fetchMerchantHashes(intent);
        else
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }


    /****************************** Client hash generation ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String Salt){
        PayuHashes payuHashes = new PayuHashes();
        postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if(postData.getCode() == PayuErrors.NO_ERROR){
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        var1 = var1 == null ? PayuConstants.DEFAULT : var1 ;

        if((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if(!var1.contentEquals(PayuConstants.DEFAULT)){
            // get user card
            if((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if(mPaymentParams.getOfferKey() != null ){
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if(postData.getCode() == PayuErrors.NO_ERROR){
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if(mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR ){
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    private void storeMerchantHash(String cardToken, String merchantHash){

        final String postParams = "merchant_key="+key+"&user_credentials="+var1+"&card_token="+cardToken+"&merchant_hash="+merchantHash;

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //  https://mobiledev.payu.in/admin/wis.php?action=add&uid=124&mid=457&token=74588&cvvhash=0123456789031

                    URL url = new URL("https://payu.herokuapp.com/store_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }




    private void fetchMerchantHashes(final Intent intent){
        // now make the api call.
        final String postParams = "merchant_key=" + key + "&user_credentials=" + var1 ;
        final Intent baseActivityIntent = intent;
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    //  https://mobiled ev.payu.in/admin/wis.php?action=add&uid=124&mid=457&token=74588&cvvhash=0123456789031

                    URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());

                    HashMap<String, String> cardTokens = new HashMap<String, String>();
                    JSONArray oneClickCardsArray = response.getJSONArray("data");
                    int arrayLength;
                    if((arrayLength = oneClickCardsArray.length()) >= 1) {
                        for (int i = 0; i < arrayLength; i++) {
                            cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                        }
                        return cardTokens;
                    }
                    // pass these to next activity

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> oneClickTokens) {
                super.onPostExecute(oneClickTokens);

                baseActivityIntent.putExtra(PayuConstants.ONE_CLICK_CARD_TOKENS, oneClickTokens);
                startActivityForResult(baseActivityIntent, PayuConstants.PAYU_REQUEST_CODE);
            }
        }.execute();
    }



    private void deleteMerchantHash(String cardToken){

        final String postParams = "card_token="+cardToken;

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //  https://mobiledev.payu.in/admin/wis.php?action=add&uid=124&mid=457&token=74588&cvvhash=0123456789031

                    URL url = new URL("https://payu.herokuapp.com/delete_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
//                    StringBuffer responseStringBuffer = new StringBuffer();
//                    byte[] byteContainer = new byte[1024];
//                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
//                        responseStringBuffer.append(new String(byteContainer, 0, i));
//                    }
//
//                    JSONObject response = new JSONObject(responseStringBuffer.toString());


//                } catch (JSONException e) {
//                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }


    public HashMap<String, String > getAllOneClickHashHelper(String merchantKey, String userCredentials) {

        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials ;
        final Intent baseActivityIntent = intent;
        HashMap<String, String> cardTokens  = new HashMap<String, String>();;

        try {
            //  https://mobiled ev.payu.in/admin/wis.php?action=add&uid=124&mid=457&token=74588&cvvhash=0123456789031

            URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

            byte[] postParamsByte = postParams.getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postParamsByte);

            InputStream responseInputStream = conn.getInputStream();
            StringBuffer responseStringBuffer = new StringBuffer();
            byte[] byteContainer = new byte[1024];
            for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                responseStringBuffer.append(new String(byteContainer, 0, i));
            }

            JSONObject response = new JSONObject(responseStringBuffer.toString());

            JSONArray oneClickCardsArray = response.getJSONArray("data");
            int arrayLength;
            if((arrayLength = oneClickCardsArray.length()) >= 1) {
                for (int i = 0; i < arrayLength; i++) {
                    cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                }

            }
            // pass these to next activity

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardTokens;
    }




    /**
     * Returns a HashMap object of cardToken and one click hash from merchant server.
     *
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * The function should return a cardToken and corresponding one click hash as a hashMap.
     *
     *@param userCreds   a string giving the user credentials of user.
     *@return            the Hash Map of cardToken and one Click hash.
     **/

    @Override
    public HashMap<String, String > getAllOneClickHash(String userCreds){
        // 1. GET http request from your server
        // GET params - merchant_key, user_credentials.
        // 2. In response we get a
        // this is a sample code for fetching one click hash from merchant server.
        return getAllOneClickHashHelper(key, userCreds);
    }

    @Override
    public void getOneClickHash(String cardToken, String merchantKey, String userCredentials) {

    }

    /**
     *
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function save the oneClickHash corresponding to its cardToken
     *
     *@param cardToken     a string containing the card token
     *@param oneClickHash  a string containing the one click hash.
     **/

    @Override
    public void saveOneClickHash(String cardToken, String oneClickHash) {
        // 1. POST http request to your server
        // POST params - merchant_key, user_credentials,card_token,merchant_hash.
        // 2. In this POST method the oneclickhash is stored corresponding to card token in merchant server.
        // this is a sample code for storing one click hash on merchant server.

        storeMerchantHash(cardToken, oneClickHash);

    }

    /**
     *
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function delete’s the oneClickHash from the merchant server
     *
     *@param cardToken     a string containing the card token
     *@param userCredentials  a string containing the user credentials.
     **/

    @Override
    public void deleteOneClickHash(String cardToken,  String userCredentials) {

        // 1. POST http request to your server
        // POST params  - merchant_hash.
        // 2. In this POST method the oneclickhash is deleted in merchant server.
        // this is a sample code for deleting one click hash from merchant server.

        deleteMerchantHash(cardToken);

    }

}







