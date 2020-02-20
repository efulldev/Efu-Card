package com.efulltech.efupay.efucard;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.multidex.MultiDex;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.efulltech.efupay.efucard.CardPaymentActivity;
import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.utils.AppPrefs;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;
import com.efulltech.efupay.efucard.utils.JsonArrResponseCallback;
import com.efulltech.efupay.efucard.utils.PushMessageReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class BaseApplication extends Application {

    private String TAG = "BASE APP";
    public static final String CHANNEL_1_ID = "channel_1";
    public static final String CHANNEL_2_ID = "channel_2";
    private static AppPrefs appPrefs;
    private static Controller controller;
    private Thread thread;
    private static NotificationManagerCompat notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appPrefs = new AppPrefs(this);

        controller = new Controller(this);
        notificationManager = NotificationManagerCompat.from(this);

        initCloudChannel(this);

    }


    /**
     * Initialize cloud push channel
     * @param applicationContext
     */
    public void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        createNotificationChannel();

        PushServiceFactory.init(applicationContext);

        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {

               Log.i(TAG, "init cloud channel success");
                Toast.makeText(applicationContext, "Init cloud channel success", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloud channel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id,
            // Notification channel id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            // The name of the notification channel that users can see
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            // Description of the notification channels that users can see
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            // Configure the properties of the notification channel
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            // Set flashing when notifications appear (if supported by android device)
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            // Set the vibration when notifications appear (if supported by the android device)
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }




    public static void initRecursiveDataSync() {
        if (appPrefs.getStringValue(Globals.MERCHANT_ID) != null && appPrefs.getStringValue(Globals.MERCHANT_CARD_SN) != null) {
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    controller.recursiveTransactDataSend();
                }
            }, 0, 30000);
        }
    }


    public static void startUssdReceiverListener(Context context, JsonArrResponseCallback callback) {

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //...
            }
        }, 0, 30000);



        appPrefs.setBooleanValue(Globals.USSD_LISTENER, true);
//        Timer t = new Timer();
//        t.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
                if(appPrefs.getBooleanValue(Globals.USSD_LISTENER)) {
//                    controller.networkGetRequest((objArr, err) -> {
//                        if (err == null) {
//                            if (objArr.length() > 0) {
//                                // end interval
//                                appPrefs.setBooleanValue(Globals.USSD_LISTENER, false);
////                                t.cancel();
//                                Log.d("USSD LISTENER", "Calcelled");
//
//                                // sound notification
//                                ussdNotification(context, "USSD Payments", objArr.length() + " payments received via USSD payment channel");
//
//                                // return data
//                                callback.done(objArr, null);
//                            }
//                        }
//                    });
                }else{
//                    t.cancel();
                    Log.d("USSD LISTENER", "Calcelled");
                }
//            }
//        }, 0, 30000);

    }

    private static void ussdNotification(Context context, String title,String message) {
        Intent resultIntent = new Intent(context, CardPaymentActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.point_blank);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.background_alert_view)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(soundUri)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1, notification);
    }
}
