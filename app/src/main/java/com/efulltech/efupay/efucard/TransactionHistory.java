package com.efulltech.efupay.efucard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.contracts.TransactionDBHelper;
import com.efulltech.efupay.efucard.models.TransactionHistoryAdapter;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionHistory extends AppCompatActivity {

    private TransactionHistoryAdapter mAdapter;
    private List<JSONObject> transactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionDBHelper mDatabase;
    private View noRecordView;
    private Controller controller;
    private AppPrefs appPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        controller = new Controller(this);
        appPrefs = new AppPrefs(this);

        recyclerView = findViewById(R.id.recycler_view);
        noRecordView = findViewById(R.id.no_record_animae);

        fetchTransactionHistory();
    }

    private void fetchTransactionHistory() {
        controller.showOperationsDialog("Fetching Transaction History", "Please wait...");
//        mDatabase = new TransactionDBHelper(this);
//        transactList.addAll(mDatabase.listTransactions());
        controller.fetchEfuPayAccessToken((accessToken, err) -> {
            if (err == null) {
                // try to login
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("accessToken", accessToken);
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("merchantId", appPrefs.getStringValue(Globals.MERCHANT_ID));
                    payload.put("merchantCardSn", appPrefs.getStringValue(Globals.MERCHANT_CARD_SN));
                    payload.put("loginName", appPrefs.getStringValue(Globals.VENDOR_ID));
                    payload.put("page", "1");
                    payload.put("size", "10");
                    payload.put("startDate", "");
                    payload.put("endDate", "");

                    controller.networkRequestArrResponse(payload, Globals.EFU_PAY_BASE_URL+"/v1/vendor/transaction/history", Request.Method.POST, headers, (resObj, e)->{
                        if(e == null) {
                            controller.dismissProgressDialog();
                            if(resObj != null){
                                Log.d("TRANS HISTORY: ", resObj.length()+"");
                                for(int i=0;i<resObj.length();i++) {
                                    try {
                                        JSONObject jsonObject = resObj.getJSONObject(i);
                                        transactList.add(jsonObject);
                                        // do something
                                    } catch (JSONException ex) {
                                        e.printStackTrace();
                                    }



                                }


                            }
                            mAdapter = new TransactionHistoryAdapter(this, transactList, mDatabase);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
//
//                            /**
//                             * On long press on RecyclerView item, open alert dialog
//                             * with options to choose
//                             * Edit and Delete
//                             * */
//                            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
//                                    recyclerView, new RecyclerTouchListener.ClickListener() {
//                                @Override
//                                public void onClick(View view, final int position) {
//                                    // navigate to single history page
//                                    // thisTransaction(position);
//                                }
//
//                                @Override
//                                public void onLongClick(View view, int position) {
//                                    // showActionsDialog(position);
//                                }
//                            }));
                        }else{
                            controller.dismissProgressDialog();
                            controller.showErrorMessage("Error fetching transaction history", e.getMessage(), null);
                        }
                        toggleEmptyNotes();
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    controller.dismissProgressDialog();
                    controller.showErrorMessage("Error fetching transaction history", e.getMessage(), null);
                }
            }else{
                controller.dismissProgressDialog();
                controller.showErrorMessage("Error fetching transaction history", err.getMessage(), null);
            }
        });
    }


    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check notesList.size() > 0
        if (transactList.size() > 0) {
            noRecordView.setVisibility(View.GONE);
        } else {
            noRecordView.setVisibility(View.VISIBLE);
        }
    }
}
