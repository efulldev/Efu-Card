package com.efulltech.efupay.efucard.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.efulltech.efupay.efucard.R;

import com.efulltech.efupay.efucard.contracts.TransactionDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import static com.efulltech.efupay.efucard.utils.Controller.formatAmount;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    private Context context;
    private List<JSONObject> notesList;
    private TransactionDBHelper mDatabase;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView refno;
        public TextView note;
        public TextView timestamp;
        public TextView method;
        public TextView appName;

        public MyViewHolder(View view) {
            super(view);
            refno = view.findViewById(R.id.refno);
            note = view.findViewById(R.id.note);
            timestamp = view.findViewById(R.id.timestamp);
            method = view.findViewById(R.id.pay_method);
            appName = view.findViewById(R.id.appNameText);
        }
    }


    public TransactionHistoryAdapter(Context context, List<JSONObject> notesList, TransactionDBHelper mDatabase) {
        this.context = context;
        this.notesList = notesList;
        this.mDatabase = mDatabase;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transact_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JSONObject transact = notesList.get(position);
        Float _amount = null;
        try {
            _amount = Float.parseFloat(transact.getString("orderAmount"));
            _amount = _amount.floatValue();
            String _msg = formatAmount(_amount);
            String _method = transact.getString("tradeType");
            String _dateTime = transact.getString("orderTime");
            String appName = transact.getString("paymentType");
            //        display ref no
            holder.refno.setText("RRN: "+transact.getString("outTradeNo"));
//        display response message
            holder.note.setText(_msg);
//        display response message
            holder.method.setText(_method);
            // Formatting and displaying timestamp
            holder.timestamp.setText(_dateTime);
//        display app name from which transaction was started from
            holder.appName.setText(appName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return notesList.size();
    }

}