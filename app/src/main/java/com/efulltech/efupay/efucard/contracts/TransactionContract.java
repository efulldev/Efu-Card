package com.efulltech.efupay.efucard.contracts;

import android.provider.BaseColumns;

public class TransactionContract {

    private TransactionContract(){

    }

    public static final class TransactionEntry implements BaseColumns{
        public static final String TRANSACTION_TABLE_NAME = "transaction_data_pool";
        public static final String PROC_ID = "process_id";
        public static final String DEVICE_ID = "device_id";
        public static final String MER_ID = "merchant_id";
        public static final String VEN_ID = "vendor_id";
        public static final String REF_NO = "reference_no";
        public static final String AMOUNT = "amount";
        public static final String DATE_TIME = "transact_date_time";
        public static final String STATUS_CODE = "transact_status_code";
        public static final String TRANSACT_INITIATOR = "transact_initiator";
        public static final String TRANSACT_PAY_METHOD = "transact_payment_method";
        public static final String DATA_PERSISTED = "data_has_been_persisted";
        public static final String MER_CARD_SN = "merchant_card_sn";
        public static final String TRX_DATE_TIME = "bank_trans_timestamp";
    }
}
