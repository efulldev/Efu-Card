package com.efulltech.efupay.efucard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {

    Handler handler;

    private SharedPreferences sharedPref;
    private Controller controller;
    private AppPrefs appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeFactorySetup();

        TextView textView = findViewById(R.id.appVersionText);
        textView.setText("v"+ BuildConfig.VERSION_NAME);
        controller = new Controller(this);
        appPrefs = new AppPrefs(this);

        String url = Globals.EFU_APPAY_BASE_URL+"/efuPayAccessCode";

        JsonObject payload = new JsonObject();
        try {
            payload.addProperty("appId", Globals.EFU_PAY_APP_ID);
            payload.addProperty("appSecret", Globals.EFU_PAY_APP_SECRET);
            payload.addProperty("sessionLength", "30");

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "application/json");

            controller.networkRequest(payload, url, Request.Method.POST, headers, (resObj, err)->{
                if(err == null){
                    if(resObj != null){
                        try {
                            String accessToken = resObj.getString("accessToken");
                            Log.d("EFU PAY ACCESS TOKEN", accessToken);
                            appPrefs.setStringValue(Globals.EFU_PAY_ACCESS_TOKEN, accessToken);
                            // get Efu Appay Token
                            JsonObject appPayload = new JsonObject();
                            appPayload.addProperty("client_id", Globals.EFU_APPAY_CLIENT_ID);
                            appPayload.addProperty("client_secret", Globals.EFU_APPAY_CLIENT_SECRET);

                            String url_2 = Globals.EFU_APPAY_BASE_URL+"/getToken";

                            controller.networkRequest(appPayload, url_2, Request.Method.POST, headers, (obj, e)->{
                                if (e == null) {
                                    if(obj != null){
                                        try {
                                            String token = obj.getString("token");
                                            Log.d("EFU APPAY TOKEN", token);
                                            appPrefs.setStringValue(Globals.EFU_APPAY_TOKEN, token);
                                            // open login screen
                                            BaseApplication.initRecursiveDataSync();
                                            Intent intent = new Intent(SplashActivity.this, MerchantLoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                            controller.showErrorMessage("Initialization Failed", ex.getMessage(), (opt)->{
                                                finish();
                                            });
                                        }
                                    }else{
                                        controller.showErrorMessage("Initialization Failed", "Failed to get token", (opt)->{
                                            finish();
                                        });
                                    }
                                }else{
                                    controller.showErrorMessage("Initialization Failed", "Failed to get token", (opt)->{
                                        finish();
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            controller.showErrorMessage("Initialization Failed", e.getMessage(), (opt)->{
                                finish();
                            });
                        }
                    }else{
                        controller.showErrorMessage("Initialization Failed", "Failed to get Access token", (opt)->{
                            finish();
                        });
                    }
                }else{
                    controller.showErrorMessage("Initialization Failed", err.getMessage(), (opt)->{
                        finish();
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            controller.showErrorMessage("Initialization Failed", e.getMessage(), (opt)->{
                finish();
            });
        }
    }

    private void initializeFactorySetup() {
        AppPrefs appPrefs = new AppPrefs(this);
        // check for app update
//        Controller controller = new Controller(SplashActivity.this);
//        controller.checkOsVersion(false, (response, e) -> {
//            // start timer to navigate to main app page
//            handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 4980);
//
//            if(e == null) {
//                Log.d("Update Response", response);
//                if (!response.equals("true")) {
//                    // launch app store
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(response)));
//                    handler = null;
//                }
//            }
//        });
    }
}
