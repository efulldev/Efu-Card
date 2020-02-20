package com.efulltech.efupay.efucard.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class AppPrefs {
    private static SharedPreferences appSharedPreferences;
    private static Context mContext;

    public AppPrefs(Context context){
        this.mContext = context;
    }

    private static SharedPreferences getAppPreferences() {
        if (appSharedPreferences == null) {
            appSharedPreferences = mContext.getSharedPreferences(Globals.APP_PREFS_NAME, Context.MODE_PRIVATE);
        }
        return appSharedPreferences;
    }

    public String getStringValue(String name){
        return getAppPreferences().getString(name, null);
    }

    @SuppressLint("ApplySharedPref")
    public void setStringValue(String name, String value) {
        getAppPreferences().edit().putString(name, value).apply();
    }

    public Integer getIntValue(String name) {
        return getAppPreferences().getInt(name, 0);
    }

    public void setIntValue(String name, Integer value) {
        getAppPreferences().edit().putInt(name, value).apply();
    }

    public boolean getBooleanValue(String value) {
        return getAppPreferences().getBoolean(value, false);
    }

    public void setBooleanValue(String name, boolean value) {
        getAppPreferences().edit().putBoolean(name, value).apply();
    }
}
