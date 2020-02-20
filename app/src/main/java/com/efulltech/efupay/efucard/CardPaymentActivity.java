package com.efulltech.efupay.efucard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CardPaymentActivity extends AppCompatActivity {

    private Controller controller;
    EditText amt;
    private AppPrefs appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);
        controller = new Controller(this);
        appPrefs = new AppPrefs(this);
        amt = findViewById(R.id.amt_edit);


        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // open card payment activity
        View card_pay_proc_btn = findViewById(R.id.card_pay_proc_btn);
        card_pay_proc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!amt.getText().toString().equals("")) {
                    Double amount = Double.parseDouble(amt.getText().toString());
                    if (amount > 0) {
                        try {
                            Intent newPaymentIntent = new Intent("com.arke.sdk.TransactParser");
                            newPaymentIntent.putExtra("trantype", 1);
                            newPaymentIntent.putExtra("batchno", 1);
                            newPaymentIntent.putExtra("seqno", 1);
                            newPaymentIntent.putExtra("amount", (int) (amount * 100));
                            newPaymentIntent.putExtra("action", "makePayment");
                            newPaymentIntent.putExtra("appName", getString(R.string.app_name));
                            newPaymentIntent.putExtra("domainName", getPackageName());
                            startActivityForResult(newPaymentIntent, Globals.NEW_PAYMENT_REQUEST_CODE);
                        } catch (ActivityNotFoundException e) {
                            controller.showErrorMessage("Oops!", "The Requested Process (com.arke.sdk.TransactParser) is probably not on this device. Please confirm and try again.", (res) -> {
                                // do something here
                            });
                        }
                    } else {
                        controller.showErrorMessage("Oops!", "Invalid amount passed. Please input an amount greater than 0", (res) -> {
                            // do something here
                        });
                    }
                } else {
                    controller.showErrorMessage("Oops!", "Invalid amount passed. Please input an amount greater than 0", (res) -> {
                        // do something here
                    });
                }
            }
        });


        // end activity
        View card_pay_cancel_btn = findViewById(R.id.card_pay_cancel_btn);
        card_pay_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmThenFinish();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        confirmThenFinish();
    }

    private void confirmThenFinish() {
        controller.showDescisionDialog("End Transaction", "Do you wish to terminate this transaction?", (response) -> {
            finish();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globals.NEW_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //We are good to go, the payment was made successfully.
                if (data == null) {
                    controller.showErrorMessage("Transaction Error", "Oops! Sorry, failed to complete card payment.Please, try again.", (res) -> {
                        // do something here
                    });
                    return;
                }
                String response = (String) data.getSerializableExtra("response");
                String responseCode = (String) data.getSerializableExtra("responseCode");
                String amount = (String) data.getSerializableExtra("amount");
                String refNo = (String) data.getSerializableExtra("refNo");
                String batchNo = (String) data.getSerializableExtra("batchNo");
                String seqNo = (String) data.getSerializableExtra("seqNo");



                Log.d("Card Response", responseCode);
                Toast.makeText(this, "Response "+responseCode, Toast.LENGTH_SHORT).show();
                if(responseCode.equals("00")){

                    // save transaction data to local data storage
                    JSONObject payload = new JSONObject();
                    try {
                        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

                        payload.put(Globals.RESPONSE_CODE, responseCode);
                        payload.put(Globals.AMOUNT, Double.parseDouble(amount));
                        payload.put(Globals.REF_NO, refNo);
                        payload.put(Globals.PROC_ID, appPrefs.getStringValue(Globals.MERCHANT_CARD_SN)+currentDate+currentTime);
                        payload.put(Globals.MERCHANT_ID, "45678987");
                        payload.put(Globals.VENDOR_ID, "63789843");
                        payload.put(Globals.TRANSACT_INIT, "Efu Card");
                        payload.put(Globals.TRANSACT_PAY_METHOD, "ATM Card");
                        controller.storeInDevice(payload, (res, err)->{
                            controller.recursiveTransactDataSend();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        controller.showErrorMessage("Transaction data backup error", e.getMessage(), null);
                    }
                }else{
                    controller.showErrorMessage("Transaction Error", response, (res)-> {
                        // do something here
                    });
                }
            }else{
                controller.showErrorMessage("Transaction Cancelled", "An error occured while processing the payment", (res) -> {
                    // do something here
                    // remove this
                    // save transaction data to local data storage
                    JSONObject _payload = new JSONObject();
                    try {
                        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
                        _payload.put(Globals.RESPONSE_CODE, "99");
                        _payload.put(Globals.AMOUNT, Double.parseDouble(amt.getText().toString()));
                        _payload.put(Globals.REF_NO, currentTime+""+currentDate);
                        _payload.put(Globals.PROC_ID, appPrefs.getStringValue(Globals.MERCHANT_CARD_SN)+currentDate+currentTime);
                        _payload.put(Globals.MERCHANT_ID, appPrefs.getStringValue(Globals.MERCHANT_ID));// 868557032907784
                        _payload.put(Globals.MERCHANT_CARD_SN, appPrefs.getStringValue(Globals.MERCHANT_CARD_SN));
                        _payload.put(Globals.VENDOR_ID, appPrefs.getStringValue(Globals.VENDOR_ID));
                        _payload.put(Globals.TRANSACT_TIMESTAMP, currentDate+currentTime);
                        _payload.put(Globals.TRANSACT_INIT, "Efu Card");
                        _payload.put(Globals.TRANSACT_PAY_METHOD, "ATM Card");
                        controller.storeInDevice(_payload, (_res, err)->{
                            controller.recursiveTransactDataSend();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        controller.showErrorMessage("Transaction data backup error", e.getMessage(), null);
                    }
                });
            }
        }
    }

}
