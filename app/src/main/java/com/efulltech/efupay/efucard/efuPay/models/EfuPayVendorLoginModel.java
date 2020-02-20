package com.efulltech.efupay.efucard.efuPay.models;

import android.content.Context;

public class EfuPayVendorLoginModel {

    private String merchantId;
    private String loginName; //vendor's login
    private String loginPassword; // vendor's password
    private String merchantCardSn; // Merchant's Efu-Card serial number
    private String deviceOs;
    private String deviceSn;
    private String deviceToken;
    private Context mContext;

    public EfuPayVendorLoginModel(Context mContext) {
        this.mContext = mContext;
    }

    public EfuPayVendorLoginModel() {

    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getMerchantCardSn() {
        return merchantCardSn;
    }

    public void setMerchantCardSn(String merchantCardSn) {
        this.merchantCardSn = merchantCardSn;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


}
