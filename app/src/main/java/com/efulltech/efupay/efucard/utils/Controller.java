package com.efulltech.efupay.efucard.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.text.Html;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.contracts.TransactionContract;
import com.efulltech.efupay.efucard.contracts.TransactionDBHelper;
import com.efulltech.efupay.efucard.efuPay.models.EfuPayVendorLoginModel;
import com.efulltech.efupay.efucard.models.EfuPayLoad;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.efulltech.efupay.efucard.BaseApplication.CHANNEL_1_ID;


public class Controller {
    private static final String TAG = "Controller";
    private static Context mContext;
    private static LottieAlertDialog dialog;

    private ProgressDialog progressDialog;
    private NotificationManagerCompat notificationManager;
    private static SQLiteDatabase mDatabase;
    private static AppPrefs appPrefs;
    private Thread thread;


    public Controller(Context context){
        mContext = context;
        notificationManager = NotificationManagerCompat.from(mContext);
        appPrefs = new AppPrefs(mContext);

        TransactionDBHelper dbHelper = new TransactionDBHelper(mContext);
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void fetchEfuPayAccessToken(ResponseCallback callback){
        // check if shared pref has access token
        String token = appPrefs.getStringValue(Globals.EFU_PAY_ACCESS_TOKEN);
        if(token.equals("")){
            // request for a new token
//            BaseApplication.getEfuPayApiToken(mContext, (accesToken, err)->{
//                callback.done(accesToken, null);
//            });
            callback.done(null, null);
        }else{
            // a validation has to be on the token
            callback.done(token, null);
        }
    }


    public void recursiveTransactDataSend(){

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TransactionContract.TransactionEntry.TRANSACTION_TABLE_NAME+" WHERE "+ TransactionContract.TransactionEntry.DATA_PERSISTED +" = ?", new String[]{"false"});
        Log.d("Entries : ", cursor.getCount()+"");
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {
                    // register each payment on efupay
                    EfuPayLoad efuPayLoad = new EfuPayLoad(mContext);

                    efuPayLoad.setProcessId(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.PROC_ID))); // outTradeNo
                    efuPayLoad.setAmount(Double.parseDouble(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.AMOUNT))));
                    efuPayLoad.setSubject("POS ATM Transaction from vendor "+appPrefs.getStringValue(Globals.VENDOR_ID));
                    efuPayLoad.setAcquirerCode("044");
                    efuPayLoad.setRefNo(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.REF_NO)));
                    efuPayLoad.setBankTrxTime(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.TRX_DATE_TIME)));
                    efuPayLoad.setMerchantId(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.MER_ID)));
                    efuPayLoad.setMerchantCardSn(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.MER_CARD_SN)));
                    efuPayLoad.setOperatorId(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.VEN_ID)));


                    efuPayLoad.persistDataOnline((res, err) -> {
                        if(err == null){
                            if(res != null){
                                // saved successfully online
                                try {
                                    Log.d("TRANS PERSIST::", res.getString("tradeNo"));
                                    // update record on device database
                                    ContentValues values = new ContentValues();
                                    values.put(TransactionContract.TransactionEntry.DATA_PERSISTED, false);
                                    mDatabase.update(TransactionContract.TransactionEntry.TRANSACTION_TABLE_NAME, values ,TransactionContract.TransactionEntry.REF_NO+" = ?", new String[]{efuPayLoad.getRefNo()});
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    // move to next row
                } while (cursor.moveToNext());
            }
        }
//        mDatabase.close();
    }



    public static void storeInDevice(JSONObject object, ResponseCallback callback){
        ContentValues content = new ContentValues();
        try {
            content.put(TransactionContract.TransactionEntry.AMOUNT, object.getDouble(Globals.AMOUNT));
            content.put(TransactionContract.TransactionEntry.REF_NO, object.getString(Globals.REF_NO));
            content.put(TransactionContract.TransactionEntry.STATUS_CODE, object.getString(Globals.RESPONSE_CODE));
            content.put(TransactionContract.TransactionEntry.PROC_ID, object.getString(Globals.PROC_ID));
            content.put(TransactionContract.TransactionEntry.DEVICE_ID, getDeviceId());
            content.put(TransactionContract.TransactionEntry.MER_ID, object.getString(Globals.MERCHANT_ID));
            content.put(TransactionContract.TransactionEntry.MER_CARD_SN, object.getString(Globals.MERCHANT_CARD_SN));
            content.put(TransactionContract.TransactionEntry.VEN_ID, object.getString(Globals.VENDOR_ID));
            content.put(TransactionContract.TransactionEntry.TRX_DATE_TIME, object.getString(Globals.TRANSACT_TIMESTAMP));
            content.put(TransactionContract.TransactionEntry.TRANSACT_INITIATOR, object.getString(Globals.TRANSACT_INIT));
            content.put(TransactionContract.TransactionEntry.TRANSACT_PAY_METHOD, object.getString(Globals.TRANSACT_PAY_METHOD));
            content.put(TransactionContract.TransactionEntry.DATA_PERSISTED, "false");

            mDatabase.insert(TransactionContract.TransactionEntry.TRANSACTION_TABLE_NAME, null, content);
            callback.done("Saved", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void declinedNotif(){
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.transaction_declined1580281671);
        try{
            mp.prepare();
            mp.start();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    public void popUpNotif(String title, String message, Intent resultIntent){
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ mContext.getApplicationContext().getPackageName() + "/" + R.raw.point_blank);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, CHANNEL_1_ID)
                .setDefaults(0)
                .setSmallIcon(R.drawable.background_alert_view)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent)
                .build();
        notificationManager.notify(1, notification);
    }

    public static void showOperationsDialog(String title, String description) {
        dialog = new LottieAlertDialog.Builder(mContext, DialogTypes.TYPE_LOADING)
                .setTitle(title)
                .setDescription(description)
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showErrorMessage(String title, String description, @Nullable final SingleInputDialogCallback callback) {
        LottieAlertDialog errorCreationErrorDialog = new LottieAlertDialog
                .Builder(mContext, DialogTypes.TYPE_ERROR)
                .setTitle(title).setDescription(description)
                .setPositiveText("OK")
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog dialog) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.done("success");
                        }
                    }
                })
                .build();
        errorCreationErrorDialog.setCancelable(false);
        errorCreationErrorDialog.show();
    }

    public static void showSuccessMessage(String title, String description, @Nullable final SingleInputDialogCallback callback) {
        LottieAlertDialog dialog = new LottieAlertDialog
                .Builder(mContext, DialogTypes.TYPE_SUCCESS)
                .setTitle(title).setDescription(description)
                .setPositiveText("OK")
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog d) {
                        d.dismiss();
                        if (callback != null) {
                            callback.done("success");
                        }
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showDescisionDialog(String title, String description, @Nullable final SingleInputDialogCallback callback) {
        LottieAlertDialog dialog = new LottieAlertDialog.Builder(mContext, DialogTypes.TYPE_QUESTION)
                .setTitle(title)
                .setDescription(description)
                .setNegativeText("Cancel")
                .setNegativeListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                    }
                })
                .setPositiveText("Proceed")
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                        callback.done(null);
                    }
                })
                .build();
        dialog.setCancelable(false);
        dialog.show();
    }


    public static void showSingleInputDialog(String _title, String _description, String editTextValue, @Nullable String animation, final SingleInputDialogCallback callback){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.single_input_dialog_layout, null);

        final EditText editText = dialogView.findViewById(R.id.val_editText);
        Button confirm = dialogView.findViewById(R.id.confirm_btn);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);

        if(animation != null){
            LottieAnimationView animationView = dialogView.findViewById(R.id.lottieAnimIcon);
            animationView.setAnimation(animation);
        }

        title.setText(_title);
        description.setText(_description);
        editText.setText(editTextValue);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                callback.done(editText.getText().toString().trim());
            }
        });

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    public void showDoubleInputDialog(String _title, String _description, String label_1_Value, String editText_1_Value, String label_2_Value, String editText_2_Value, @Nullable String animation, final DoubleInputDialogCallback callback){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.double_input_dialog_layout, null);

        final EditText editText_1 = dialogView.findViewById(R.id.editText_1);
        final EditText editText_2 = dialogView.findViewById(R.id.editText_2);
        Button confirm = dialogView.findViewById(R.id.confirm_btn);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);
        TextView label_1 = dialogView.findViewById(R.id.label_1);
        TextView label_2 = dialogView.findViewById(R.id.label_2);

        if(animation != null){
            LottieAnimationView animationView = dialogView.findViewById(R.id.lottieAnimIcon);
            animationView.setAnimation(animation);
        }

        title.setText(_title);
        description.setText(_description);
        label_1.setText(label_1_Value);
        label_2.setText(label_2_Value);
        editText_1.setText(editText_1_Value);
        editText_2.setText(editText_2_Value);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                callback.done(editText_1.getText().toString().trim(), editText_2.getText().toString().trim());
            }
        });

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    public void showTripleInputDialog(String _title, String _description, String label_1_Value, String editText_1_Value, String label_2_Value, String editText_2_Value, String label_3_Value, String editText_3_Value, @Nullable String animation, @Nullable int inputType, final TripleInputDialogCallback callback){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.triple_input_dialog_layout, null);

        final EditText editText_1 = dialogView.findViewById(R.id.editText_1);
        final EditText editText_2 = dialogView.findViewById(R.id.editText_2);
        final EditText editText_3 = dialogView.findViewById(R.id.editText_3);

        Button confirm = dialogView.findViewById(R.id.confirm_btn);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);
        TextView label_1 = dialogView.findViewById(R.id.label_1);
        TextView label_2 = dialogView.findViewById(R.id.label_2);
        TextView label_3 = dialogView.findViewById(R.id.label_3);

        if(animation != null){
            LottieAnimationView animationView = dialogView.findViewById(R.id.lottieAnimIcon);
            animationView.setAnimation(animation);
        }


        if(inputType == InputType.TYPE_NUMBER_VARIATION_PASSWORD){
            LottieAnimationView animationView = dialogView.findViewById(R.id.lottieAnimIcon);
            animationView.setAnimation("lf30_editor_fqtptJ.json");
            editText_1.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            editText_1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText_1.setTransformationMethod(PasswordTransformationMethod.getInstance());

            editText_2.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            editText_2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText_2.setTransformationMethod(PasswordTransformationMethod.getInstance());

            editText_3.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            editText_3.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText_3.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        title.setText(_title);
        description.setText(_description);
        label_1.setText(label_1_Value);
        label_2.setText(label_2_Value);
        label_3.setText(label_3_Value);

        editText_1.setText(editText_1_Value);
        editText_2.setText(editText_2_Value);
        editText_3.setText(editText_3_Value);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                callback.done(editText_1.getText().toString().trim(), editText_2.getText().toString().trim(), editText_3.getText().toString().trim());
            }
        });

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }



    public void showQuadInputDialog(String _title, String _description, String label_1_Value, String editText_1_Value, String label_2_Value, String editText_2_Value, String label_3_Value, String editText_3_Value, String label_4_Value, String editText_4_Value, @Nullable String animation, final QuadInputDialogCallback callback){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(mContext).create();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.quad_input_dialog_layout, null);

        final EditText editText_1 = dialogView.findViewById(R.id.editText_1);
        final EditText editText_2 = dialogView.findViewById(R.id.editText_2);
        final EditText editText_3 = dialogView.findViewById(R.id.editText_3);
        final EditText editText_4 = dialogView.findViewById(R.id.editText_4);

        Button confirm = dialogView.findViewById(R.id.confirm_btn);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);
        TextView label_1 = dialogView.findViewById(R.id.label_1);
        TextView label_2 = dialogView.findViewById(R.id.label_2);
        TextView label_3 = dialogView.findViewById(R.id.label_3);
        TextView label_4 = dialogView.findViewById(R.id.label_4);

        if(animation != null){
            LottieAnimationView animationView = dialogView.findViewById(R.id.lottieAnimIcon);
            animationView.setAnimation(animation);
        }

        title.setText(_title);
        description.setText(_description);
        label_1.setText(label_1_Value);
        label_2.setText(label_2_Value);
        label_3.setText(label_3_Value);
        label_4.setText(label_4_Value);

        editText_1.setText(editText_1_Value);
        editText_2.setText(editText_2_Value);
        editText_3.setText(editText_3_Value);
        editText_4.setText(editText_4_Value);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                callback.done(editText_1.getText().toString().trim(), editText_2.getText().toString().trim(), editText_3.getText().toString().trim(), editText_4.getText().toString().trim());
            }
        });

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }



    public static void dismissProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public void networkRequest(Object object, String endpoint_url, int requestMenthod, Map<String, String> headers, JsonResponseCallback callback) throws JSONException {
        JSONObject payload = null;
        if(object != null){
            Log.d("NET REQ OBJECT:", object.toString());
            Gson gson = new Gson();
            String jsonString = gson.toJson(object);
            payload = new JSONObject(jsonString);
        }
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMenthod, endpoint_url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // handle response
                Log.d("NET REQ RES: ", response.toString());
                callback.done(response, null);
            }
        }, error -> {
            // TODO: Handle error
            error.printStackTrace();
            Log.d("NET REQ ERR: ", error.toString());
            callback.done(null, error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                // dynamically append headers
//                if(headers != null) {
                    Iterator head = headers.entrySet().iterator();
                    while (head.hasNext()) {
                        Map.Entry pair = (Map.Entry) head.next();
                        params.put(pair.getKey().toString(), pair.getValue().toString());
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        head.remove(); // avoids a ConcurrentModificationException
                    }
//                }
                return params;
            }
        };

        // set retry policy to determine how long volley should wait before resending a failed request
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // add jsonObjectRequest to the queue
        requestQueue.add(jsonObjectRequest);
    }

    public void networkRequestArrResponse(JSONObject payload, String endpoint_url, int requestMenthod, Map<String, String> headers, JsonArrResponseCallback callback){

        JsonArrayRequestMod request = new JsonArrayRequestMod(
                requestMenthod, endpoint_url, payload, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    Log.d("Your Array Response", response.toString());
                    callback.done(response, null);
                } else {
                    Log.e("Your Array Response", "Data Null");
                    Exception ex = new Exception("Data Null");
                    callback.done(null, ex);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                callback.done(null, error);
            }
        }) {
            // headers are set her
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                // dynamically append headers
                Iterator head = headers.entrySet().iterator();
                while (head.hasNext()) {
                    Map.Entry pair = (Map.Entry) head.next();
                    params.put(pair.getKey().toString(), pair.getValue().toString());
                    head.remove(); // avoids a ConcurrentModificationException
                }
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(request);
    }


    public static String formatAmount(double totalAuthAmount) {
        String Currency = ""+ Html.fromHtml("&#8358;");
        String Separator = ",";
        Boolean Spacing = false;
        Boolean Delimiter = false;
        Boolean Decimals = true;
        String currencyFormat = "";
        if (Spacing) {
            if (Delimiter) {
                currencyFormat = Currency + ". ";
            } else {
                currencyFormat = Currency + " ";
            }
        } else if (Delimiter) {
            currencyFormat = Currency + ".";
        } else {
            currencyFormat = Currency;
        }

        String tformatted = NumberFormat.getCurrencyInstance().format(totalAuthAmount /*/ 100.0D*/).replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(), currencyFormat);
        return tformatted;
    }

    public void vendorAuth(EfuPayVendorLoginModel loginModel, JsonResponseCallback callback) {
         fetchEfuPayAccessToken((accessToken, err) -> {
                if(err == null){
                    // try to login
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("accessToken", accessToken);
                    JsonObject object = new JsonObject();
                    object.addProperty("loginName", loginModel.getLoginName());
                    object.addProperty("loginPassword", loginModel.getLoginPassword());
                    object.addProperty("merchantId", loginModel.getMerchantId());
                    object.addProperty("merchantCardSn", loginModel.getMerchantCardSn());
                    object.addProperty("deviceOs", loginModel.getDeviceOs());
                    object.addProperty("deviceSn", loginModel.getDeviceSn());
                    try {
                        networkRequest(object, Globals.EFU_PAY_BASE_URL+"/v1/vendor/login", Request.Method.POST, headers, (resObj, e)->{
                            if(e == null){
                                callback.done(resObj, null);
                            }else{
                                callback.done(null, null);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.done(null, e);
                    }
                }else{
                    // unable to get a valid token
                    callback.done(null, err);
                }
            });
    }

    public void merchantCheck(String phoneNo, JsonResponseCallback callback) {
        fetchEfuPayAccessToken((accessToken, err) -> {
            if (err == null) {
                // try to login
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("accessToken", accessToken);

                try {
                    networkRequest(null, Globals.EFU_PAY_BASE_URL+"/v1/account/"+phoneNo, Request.Method.GET, headers, (resObj, e)->{
                        if(e == null){
                            callback.done(resObj, null);
                        }else{
                            callback.done(null, null);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.done(null, e);
                }
            }
        });
    }

    public void merchantWalletCheck(String cardSn, JsonResponseCallback callback) {
        fetchEfuPayAccessToken((accessToken, err) -> {
            if (err == null) {
                // try to login
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("accessToken", accessToken);

                try {
                    networkRequest(null, Globals.EFU_PAY_BASE_URL+"/v1/account/efucard/"+cardSn, Request.Method.GET, headers, (resObj, e)->{
                        if(e == null){
                            try {
                                if(resObj.getString("userId").equals(appPrefs.getStringValue(Globals.MERCHANT_ID))){
                                    callback.done(resObj, null);
                                }else{
                                    Exception ex = new Exception("Wallet does not exist");
                                    callback.done(null, ex);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                callback.done(null, ex);
                            }
                        }else{
                            callback.done(null, null);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.done(null, e);
                }
            }
        });
    }
}
