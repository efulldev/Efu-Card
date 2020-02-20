package com.efulltech.efupay.efucard.contracts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.efulltech.efupay.efucard.models.EfuPayLoad;

import java.util.ArrayList;
import java.util.List;

import static com.efulltech.efupay.efucard.contracts.TransactionContract.*;

public class TransactionDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "transaction_pool_db";
    public static final int DATABASE_VERSION = 1;

    public TransactionDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE = "CREATE TABLE "
                + TransactionEntry.TRANSACTION_TABLE_NAME +" ("+
                TransactionEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TransactionEntry.DEVICE_ID+" TEXT NOT NULL, "+
                TransactionEntry.PROC_ID+" TEXT NOT NULL, "+
                TransactionEntry.REF_NO+" TEXT NOT NULL, "+
                TransactionEntry.MER_ID+" TEXT NOT NULL, "+
                TransactionEntry.MER_CARD_SN+" TEXT NOT NULL, "+
                TransactionEntry.VEN_ID+" TEXT NOT NULL, "+
                TransactionEntry.TRANSACT_INITIATOR+" TEXT NOT NULL, "+
                TransactionEntry.TRANSACT_PAY_METHOD+" TEXT NOT NULL, "+
                TransactionEntry.STATUS_CODE+" TEXT NOT NULL, "+
                TransactionEntry.TRX_DATE_TIME+" TEXT NOT NULL, "+
                TransactionEntry.DATE_TIME+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                TransactionEntry.AMOUNT+" DOUBLE NOT NULL, "+
                TransactionEntry.DATA_PERSISTED+" TEXT NOT NULL"+
                ");";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TransactionEntry.TRANSACTION_TABLE_NAME);
        onCreate(db);
    }

    public List<EfuPayLoad> listTransactions() {
        String sql = "SELECT * FROM "+TransactionContract.TransactionEntry.TRANSACTION_TABLE_NAME+" ORDER BY _id DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        List<EfuPayLoad> transactions = new ArrayList();
        Cursor cursor = db.rawQuery(sql, (String[])null);
        // if Cursor is contains results
        if (cursor != null) {
            // move cursor to first row
            if (cursor.moveToFirst()) {
                do {
                    Double amount = Double.parseDouble(cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.AMOUNT)));
                    String origin = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.TRANSACT_INITIATOR));
                    String ref_no = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.REF_NO));
                    String response_code = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.STATUS_CODE));
                    String deviceId = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.DEVICE_ID));
                    String merchantId =cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.MER_ID));
                    String merchantCardSn =cursor.getString(cursor.getColumnIndex(TransactionEntry.MER_CARD_SN));
                    String processId = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.PROC_ID));
                    String payMethod = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.TRANSACT_PAY_METHOD));
                    String timeStamp = cursor.getString(cursor.getColumnIndex(TransactionContract.TransactionEntry.DATE_TIME));

                    transactions.add(new EfuPayLoad(amount, origin, ref_no, response_code, deviceId, merchantId, processId, payMethod,timeStamp));

                    // move to next row
                } while (cursor.moveToNext());
            }
        }
        return transactions;
    }
}
