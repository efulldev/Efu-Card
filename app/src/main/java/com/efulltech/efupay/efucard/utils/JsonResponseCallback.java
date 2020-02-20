package com.efulltech.efupay.efucard.utils;

import org.json.JSONObject;

public interface JsonResponseCallback {
    void done(JSONObject obj, Exception e);

}
