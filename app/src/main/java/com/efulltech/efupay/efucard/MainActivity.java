package com.efulltech.efupay.efucard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.efulltech.efupay.efucard.R;
import com.efulltech.efupay.efucard.utils.Controller;
import com.efulltech.efupay.efucard.utils.Globals;
import com.efulltech.efupay.efucard.utils.MediaPlayerService;

public class MainActivity extends AppCompatActivity {

    private Controller controller;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new Controller(this);
        notificationManager = NotificationManagerCompat.from(this);



        // open card payment options
        View card_pay_btn = findViewById(R.id.purchase);
        card_pay_btn.setOnClickListener(v -> togglePaymentOptions());


        // open card payment activity
        View history_btn = findViewById(R.id.history);
        history_btn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionHistory.class);
            startActivity(intent);
        });

    }


    private void togglePaymentOptions() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate( R.layout.payment_options_selector, null );

        View smartCard = dialogView.findViewById(R.id.smartCardBtn);
        View ussd = dialogView.findViewById(R.id.ussdBtn);
        View bankApp = dialogView.findViewById(R.id.bankAppBtn);


        smartCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Intent intent = new Intent(MainActivity.this, CardPaymentActivity.class);
                startActivity(intent);
            }
        });

        ussd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Intent intent = new Intent(MainActivity.this, MmoPaymentActivity.class);
                intent.putExtra("method", Globals.USSD);
                startActivity(intent);
//                popUpNotif("USSD", "Payment received from 08179144067");
            }
        });

        bankApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
                Controller.showOperationsDialog("Receive payment via Mobile Banking App", "Listening for transactions... ");
                BaseApplication.startUssdReceiverListener(MainActivity.this, (objArr, err)->{
                    Controller.dismissProgressDialog();
                    if(objArr.length() > 0){
                        Controller.showSuccessMessage("Transaction data received", objArr.length()+" transactions received", (res)->{

                        });
                    }
                });
            }
        });

        dialogBuilder.setCancelable(true);
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }




    public void popUpNotif(String title, String message){
//        Intent resultIntent = new Intent(MainActivity.this, CardPaymentActivity.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.point_blank);
//        // Create the TaskStackBuilder and add the intent, which inflates the back stack
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntentWithParentStack(resultIntent);
//        // Get the PendingIntent containing the entire back stack
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
//                .setSmallIcon(R.drawable.background_alert_view)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setSound(soundUri)
//                .setContentIntent(resultPendingIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
//                .setOnlyAlertOnce(true)
//                .setAutoCancel(true)
//                .build();
//        notificationManager.notify(1, notification);
//        controller.declinedNotif();
        MediaPlayerService mediaPlayerService = new MediaPlayerService();
        mediaPlayerService.playSong(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.transaction_declined1580281671);
    }

    @Override
    public void onBackPressed(){
        controller.showDescisionDialog("Exit App", "Do you wish to exit?", (response) -> {
            finish();
        });
    }
}
