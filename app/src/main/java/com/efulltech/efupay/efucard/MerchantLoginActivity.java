package com.efulltech.efupay.efucard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.efuPay.models.EfuPayVendorLoginModel;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;

import org.json.JSONException;


public class MerchantLoginActivity extends AppCompatActivity {

    private Controller controller;
    private AppPrefs appPrefs;
    Button merchant_switch;
    TextView merchant_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);
        controller = new Controller(this);
        appPrefs = new AppPrefs(this);

        EditText vendor_id = findViewById(R.id.ven_id_input);
        EditText vendor_pwd = findViewById(R.id.ven_pwd_input);
        merchant_switch = findViewById(R.id.change_merchant);
        merchant_name = findViewById(R.id.merchant_name);

        // check if merchant account has been configured
        merchantBindCheck();

        // login to the app
        Button val_btn = findViewById(R.id.acc_validate_btn);
        val_btn.setOnClickListener(v -> {
            String vendor_login = vendor_id.getText().toString();
            String vendor_pass = vendor_pwd.getText().toString();
            // check that the app has been bound to a merchant's wallet
            if(appPrefs.getStringValue(Globals.MERCHANT_ID) != null && appPrefs.getStringValue(Globals.MERCHANT_CARD_SN) != null){
                if (!vendor_login.equals("") && !vendor_pass.equals("")) {
                    controller.showOperationsDialog("Validating", "Checking credentials, Please wait...");

                    EfuPayVendorLoginModel loginModel = new EfuPayVendorLoginModel(MerchantLoginActivity.this);
                    loginModel.setMerchantId(appPrefs.getStringValue(Globals.MERCHANT_ID));
                    loginModel.setMerchantCardSn(appPrefs.getStringValue(Globals.MERCHANT_CARD_SN));
                    loginModel.setLoginName(vendor_login);
                    loginModel.setLoginPassword(vendor_pass);
                    loginModel.setDeviceOs("android");
                    loginModel.setDeviceSn("54324567654");

                    controller.vendorAuth(loginModel, (obj, err) -> {
                        controller.dismissProgressDialog();
                        if (err == null) {
                            if (obj != null) {
                                try {
                                    appPrefs.setStringValue(Globals.VENDOR_ID, obj.getString("loginName"));
                                    appPrefs.setStringValue(Globals.VENDOR_PHONE, obj.getString("vendorPhone"));
                                    appPrefs.setStringValue(Globals.VENDOR_NAME, obj.getString("vendorName"));
                                    appPrefs.setIntValue(Globals.VENDOR_TYPE, obj.getInt("vendorType"));

                                    // bind account
                                    final CloudPushService pushService = PushServiceFactory.getCloudPushService();
                                    pushService.bindAccount(appPrefs.getStringValue(Globals.MERCHANT_CARD_SN), new CommonCallback() {
                                        @Override
                                        public void onSuccess(String s) {
                                            Log.d("BASE APP", "bind account " + appPrefs.getStringValue(Globals.VENDOR_ID) + " success\n");
                                            Toast.makeText(MerchantLoginActivity.this, "Account bound", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailed(String errorCode, String errorMsg) {
                                            Log.d("BASE APP", "bind account " + appPrefs.getStringValue(Globals.VENDOR_ID) + " failed." +
                                                    "errorCode: " + errorCode + ", errorMsg:" + errorMsg);
                                            Toast.makeText(MerchantLoginActivity.this, "Account bind failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // grant user access
                                    Intent intent = new Intent(MerchantLoginActivity.this, MainActivity.class);
                                    controller.dismissProgressDialog();
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    controller.showErrorMessage("Authentication Error", "Error handling login credentials", null);
                                }
                            } else {
                                controller.showErrorMessage("Authentication Failed", "Invalid login credentials", null);
                            }
                        } else {
                            controller.showErrorMessage("Authentication Failed", "Error encountered", null);
                        }
                    });
                } else {
                    controller.showErrorMessage("Authentication Error", "Invalid login credentials", null);
                }
            }else{
                controller.showErrorMessage("Authentication Error", "Merchant wallet not bound", null);
            }
        });

        // switch merchant account
        merchant_switch.setOnClickListener(v -> {
            toggleMerchantPhoneDialog();
        });

    }



    private void toggleMerchantPhoneDialog() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate( R.layout.single_input_dialog_layout, null );

        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);
        EditText phone = dialogView.findViewById(R.id.val_editText);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        Button confirm = dialogView.findViewById(R.id.confirm_btn);

        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        /////////////////////////
        // remove this
        phone.setText("03588100011");
        /////////////////////////
        title.setText("Merchant Binding");
        description.setText("Kindly input Merchant's Phone number");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appPrefs.getStringValue(Globals.MERCHANT_ID) != null) {
                    dialogBuilder.dismiss();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = phone.getText().toString();
                if(!phoneNo.equals("")){
                    dialogBuilder.dismiss();
                    controller.showOperationsDialog("Merchant Binding", "Verifying merchant account... Please wait");
                    controller.merchantCheck(phoneNo, (objRes, err)->{
                        controller.dismissProgressDialog();
                        if(err == null){
                            if(objRes != null){
                                try {
                                    appPrefs.setStringValue(Globals.MERCHANT_ID, objRes.getString("userId"));
                                    appPrefs.setStringValue(Globals.MERCHANT_LOGIN_ID, objRes.getString("loginName"));
                                    appPrefs.setStringValue(Globals.MERCHANT_PHONE_NO, objRes.getString("phoneNumber"));
                                    appPrefs.setStringValue(Globals.MERCHANT_NAME, objRes.getString("companyName"));
                                    appPrefs.setStringValue(Globals.MERCHANT_CARD_SN, null); // reset wallet card sn
                                    controller.showSuccessMessage("Merchant Binding successful", "You are now attempting to link this payment terminal to "+objRes.getString("companyName"), res ->{
//                                        merchantBindCheck();
                                        toggleMerchantCardBind();
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    controller.showErrorMessage("Merchant Binding Failed", "Error encountered getting data", (res)->{
                                        toggleMerchantPhoneDialog();
                                    });
                                }
                            }else{
                                controller.showErrorMessage("Merchant Binding Failed", "Merchant does not exist", (res)->{
                                    toggleMerchantPhoneDialog();
                                });
                            }
                        }else{
                            controller.showErrorMessage("Merchant Binding Failed", "Merchant does not exist", (res)->{
                                toggleMerchantPhoneDialog();
                            });
                        }
                    });
                }
            }
        });

        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private void toggleMerchantCardBind() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate( R.layout.single_input_dialog_layout, null );

        TextView title = dialogView.findViewById(R.id.title);
        TextView description = dialogView.findViewById(R.id.description);
        EditText cardSn = dialogView.findViewById(R.id.val_editText);
        Button cancel = dialogView.findViewById(R.id.cancel_btn);
        Button confirm = dialogView.findViewById(R.id.confirm_btn);

        cardSn.setInputType(InputType.TYPE_CLASS_NUMBER);
        /////////////////////////
        // remove this
        cardSn.setText("1100000072");
        /////////////////////////
        title.setText("Merchant Wallet Binding\n"+appPrefs.getStringValue(Globals.MERCHANT_NAME));
        description.setText("Kindly input Merchant's Wallet's Card Serial Number");


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(appPrefs.getStringValue(Globals.MERCHANT_ID) != null) {
                    dialogBuilder.dismiss();
                }
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String walletCardSn = cardSn.getText().toString();
               if (!walletCardSn.equals("")) {
                   dialogBuilder.dismiss();
                   controller.showOperationsDialog("Merchant Wallet Binding", "Attempting merchant wallet binding... Please wait");
                   controller.merchantWalletCheck(walletCardSn, (res, err)-> {
                        controller.dismissProgressDialog();
                        if(err == null){
                            if(res != null){
                                appPrefs.setStringValue(Globals.MERCHANT_CARD_SN, walletCardSn);
                                controller.showSuccessMessage("Merchant Wallet Binding Successful", "Wallet, with card number: "+walletCardSn+" has been bound to this terminal", (r3)->{
                                    merchantBindCheck();
                                });
                            }else{
                                controller.showErrorMessage("Merchant Wallet Binding Failed", "Could not get required response", (r2)->{
                                    toggleMerchantCardBind();
                                });
                            }
                        }else{
                            controller.showErrorMessage("Merchant Wallet Binding Failed", err.getMessage(), (r)->{
                                toggleMerchantCardBind();
                            });
                        }
                   });
               }
           }
       });


        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    private void merchantBindCheck() {
        // show merchant login
        if(appPrefs.getStringValue(Globals.MERCHANT_ID) == null){
            toggleMerchantPhoneDialog();
            merchant_switch.setVisibility(View.GONE);
            merchant_name.setText(null);
        }
        else if(appPrefs.getStringValue(Globals.MERCHANT_CARD_SN) == null){
            toggleMerchantCardBind();
            merchant_name.setText(appPrefs.getStringValue(Globals.MERCHANT_NAME));
        }
        else{
            merchant_name.setText(appPrefs.getStringValue(Globals.MERCHANT_NAME)+" / "+appPrefs.getStringValue(Globals.MERCHANT_CARD_SN));
            merchant_switch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed(){
        controller.showDescisionDialog("Exit App", "Do you wish to exit?", (response) -> {
            finish();
        });
    }

}
