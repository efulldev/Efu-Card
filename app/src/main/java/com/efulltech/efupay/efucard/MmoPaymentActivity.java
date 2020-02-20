package com.efulltech.efupay.efucard;

import android.content.Intent;
import android.os.Bundle;

import com.efulltech.efupay.efucard.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class MmoPaymentActivity extends AppCompatActivity {
    private View loader_animae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmo_payment);

        Intent data = getIntent();
        String method = data.getStringExtra("method");

        loader_animae = findViewById(R.id.loader_animae);

        prepareReceiver(method);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void prepareReceiver(String method) {
        switch (method){
            case "USSD":
                BaseApplication.startUssdReceiverListener(this, (objArr, e) -> {
                    if(objArr.length() > 0) {
                        // handle result
                        loader_animae.setVisibility(View.GONE);
                    }
                });
                break;
            default:
                // do something
                break;
        }
    }


}
