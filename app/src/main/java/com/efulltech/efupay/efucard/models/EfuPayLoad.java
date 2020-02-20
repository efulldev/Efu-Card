package com.efulltech.efupay.efucard.models;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;
import com.efulltech.efupay.efucard.utils.JsonResponseCallback;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EfuPayLoad {
    private String refNo;
    private double amount;
    private String responseCode;
    private String origin;
    private String deviceId;
    private String walletPinHash;
    private String externalAppId;
    private Context mContext;
    private String merchantId;
    private String processId;
    private Controller controller;
    private AppPrefs appPrefs;
    private String payMethod;
    private String timeStamp;

    private String sellerId; // receiving merchant id
    private String subject;
    private String acquirerCode;
    private String bankTrxNo;
    private String bankTrxTime;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAcquirerCode() {
        return acquirerCode;
    }

    public void setAcquirerCode(String acquirerCode) {
        this.acquirerCode = acquirerCode;
    }

    public String getBankTrxNo() {
        return bankTrxNo;
    }

    public void setBankTrxNo(String bankTrxNo) {
        this.bankTrxNo = bankTrxNo;
    }

    public String getBankTrxTime() {
        return bankTrxTime;
    }

    public void setBankTrxTime(String bankTrxTime) {
        this.bankTrxTime = bankTrxTime;
    }

    public String getMerchantCardSn() {
        return merchantCardSn;
    }

    public void setMerchantCardSn(String merchantCardSn) {
        this.merchantCardSn = merchantCardSn;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    private String merchantCardSn; // Efu-card sn bound to the merchant’s POS
    private String operatorId; // merchant vendor login name

    public EfuPayLoad(Context context){
        super();
        mContext = context;
        controller = new Controller(mContext);
        appPrefs = new AppPrefs(mContext);
    }

    // contructor for ATM transaction data to be sent to EfuPay API
    public EfuPayLoad(String refNo, int amount, String responseCode, String origin, String deviceId) {
        this.refNo = refNo;
        this.amount = amount;
        this.responseCode = responseCode;
        this.origin = origin;
        this.deviceId = deviceId;
    }

    // constructor for Wallet Binding
    public EfuPayLoad(String deviceId, String walletPinHash, String externalAppId) {
        this.deviceId = deviceId;
        this.walletPinHash = walletPinHash;
        this.externalAppId = externalAppId;
    }

    public EfuPayLoad(Double amount, String origin, String ref_no, String response_code, String deviceId, String merchantId, String processId, String payMethod, String timeStamp) {
        this.amount = amount;
        this.origin = origin;
        this.refNo = ref_no;
        this.responseCode = response_code;
        this.deviceId = deviceId;
        this.merchantId = merchantId;
        this.processId = processId;
        this.payMethod = payMethod;
        this.timeStamp = timeStamp;
    }

    public String getWalletPinHash() {
        return walletPinHash;
    }

    public void setWalletPinHash(String walletPinHash) {
        this.walletPinHash = walletPinHash;
    }

    public String getExternalAppId() {
        return externalAppId;
    }

    public void setExternalAppId(String externalAppId) {
        this.externalAppId = externalAppId;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }

    public String getPayMethod() { return payMethod; }

    public void setTimeStamp(String timeStamp){ this.timeStamp = timeStamp; }

    public String getTimestamp() { return this.timeStamp; }



    public void persistDataOnline(JsonResponseCallback callback){
        Log.d("EFU PAYLOAD", "Persisting transaction " + this.refNo);
        // generate RSA signature
        JsonObject rsaBody = new JsonObject();
        rsaBody.addProperty("message", this.processId + "" + this.merchantId + "" + this.amount);
        controller.fetchEfuPayAccessToken((accessToken, err) -> {
            if(err == null) {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Landi/A8");

                try {
                    controller.networkRequest(rsaBody, Globals.EFU_APPAY_BASE_URL + "/rsa", Request.Method.POST, headers, (res, e) -> {
                        if (e == null) {
                            String url = Globals.EFU_PAY_BASE_URL + "/v1/trade/pos/atm-pay";
                            JSONObject payload = new JSONObject();

                            try {
                                payload.put("outTradeNo", this.processId);
                                payload.put("sellerId", this.merchantId);
                                payload.put("totalAmount", this.amount);
                                payload.put("subject", this.subject);
                                payload.put("acquirerCode", this.acquirerCode);
                                payload.put("bankTrxNo", this.refNo);
                                payload.put("bankTrxTime", this.bankTrxTime);
                                payload.put("merchantCardSn", this.merchantCardSn);
                                payload.put("operatorId", this.operatorId);
                                payload.put("sign", res.getString("signature"));


                                Log.d("EFU PAYLOAD OBJECT:", payload.toString());
                                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // handle response
                                        callback.done(response, null);
                                    }
                                }, error -> {
                                    // TODO: Handle error
                                    error.printStackTrace();
                                    Log.d("EFU PAYLOAD", error.toString());
                                    callback.done(null, error);
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Accept", "application/json");
                                        params.put("Content-Type", "application/json");
                                        params.put("accessToken", accessToken);
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
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }else{
                            callback.done(null, err);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
