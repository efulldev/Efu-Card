package com.efulltech.efupay.efucard.utils;

import org.json.JSONArray;

public interface JsonArrResponseCallback {
    void done(JSONArray objArr, Exception e);
}
