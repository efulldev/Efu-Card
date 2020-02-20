package com.efulltech.efupay.efucard.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.AliyunMessageIntentService;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

import static com.efulltech.efupay.efucard.utils.PushMessageReceiver.REC_TAG;


/**
 * In order to avoid the small probability event that the push broadcast is intercepted by the system, we recommend that users handle message intermodulation through IntentService, access steps:
 * 1. Create IntentService and inherit AliyunMessageIntentService
 * 2. Override related methods and register the service with Manifest
 * 3. Call interface CloudPushService.setPushIntentService
 * For detailed users, please refer to: https://help.aliyun.com/document_detail/30066.html#h2-2-messagereceiver-aliyunmessageintentservice
 */

public class MyMessageIntentService extends AliyunMessageIntentService {
    private static final String TAG = "MyMessageIntentService";

    /**
     * Callback method for push notifications
     * @param context
     * @param title
     * @param summary
     * @param extraMap
     */
    @Override
    protected void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        Log.i(REC_TAG,"Received a push notification ： " + title + ", summary:" + summary);
    }

    /**
     * Callback method for push messages
     * @param context
     * @param cPushMessage
     */
    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
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
    protected void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        Log.i(REC_TAG,"onNotificationOpened ： " + " : " + title + " : " + summary + " : " + extraMap);
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

    /**
     * Notification delete callback
     * @param context
     * @param messageId
     */
    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        Log.i(REC_TAG, "onNotificationRemoved ： " + messageId);
    }

    /**
     * The notification arrives when the app is in the foreground. Note: This method is only valid for custom style notifications. For details, please refer to https://help.aliyun.com/document_detail/30066.html#h3-3-4-basiccustompushnotification-api
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
}
