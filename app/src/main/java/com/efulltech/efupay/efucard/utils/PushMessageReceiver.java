package com.efulltech.efupay.efucard.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

public class PushMessageReceiver extends MessageReceiver {
    public static final String REC_TAG = "receiver";

    /**
     * Callback method for push notifications
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        if ( null != extraMap ) {
            for (Map.Entry<String, String> entry : extraMap.entrySet()) {
                Log.i(REC_TAG,"@Get diy param : Key=" + entry.getKey() + " , Value=" + entry.getValue());
            }
        } else {
            Log.i(REC_TAG,"@Receive notification && Custom message is empty");
        }
        Log.i(REC_TAG,"Received a push notification ： " + title + ", summary:" + summary);
    }

    /**
     * The notification arrives when the app is in the foreground. Note: This method is only valid for custom style notifications.For details, please refer to https://help.aliyun.com/document_detail/30066.html?spm=5176.product30047.6.620.wjcC87#h3-3-4-basiccustompushnotification-api
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     * @param openType
     * @param openActivity
     * @param openUrl
     */
    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        Log.i(REC_TAG,"onNotificationReceivedInApp ： " + " : " + title + " : " + summary + "  " + extraMap + " : " + openType + " : " + openActivity + " : " + openUrl);
    }

    /**
     * Callback method for push messages
     * @param context
     * @param cPushMessage
     */
    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        Log.i(REC_TAG,"Received a push message ： " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    /**
     * Extended processing for opening notifications from the notification bar
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG,"onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

    /**
     * Notification delete callback
     * @param context
     * @param messageId
     */
    @Override
    public void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
    }

    /**
     * No action notification click callback. When the notification action specified in the background or the Alibaba Cloud console is a logical jump, the notification click callback is onNotificationClickedWithNoAction instead of onNotificationOpened
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG,"onNotificationClickedWithNoAction ： " + " : " + title + " : " + summary + " : " + extraMap);
    }

}
