package com.talla.santhamarket.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.talla.santhamarket.R;
import com.talla.santhamarket.activities.HomeActivity;
import com.talla.santhamarket.models.Data;
import com.talla.santhamarket.models.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Reader;
import java.util.Map;

public class FirebaseReceiveMessagingService extends FirebaseMessagingService {
    private static String TAG = "FirebaseReceiveMessagingService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
//            Map<String, String> params = remoteMessage.getData();
//            for (String key : params.keySet()) {
////                Log.d(TAG, "onMessageReceived: " + "Message Key : " + key + " Data : " + remoteMessage.getData().get(key));
//            }
            String title=remoteMessage.getData().get("title");
            String message=remoteMessage.getData().get("body");
            String openScreen=remoteMessage.getData().get("openScreen");
            sendNotification(title,message,openScreen);
        }
    }


    private void sendNotification(String title,String message,String openScreen) {
//        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
//        style.bigPicture(bitmap);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent =null;
        if (openScreen.equalsIgnoreCase(getApplicationContext().getString(R.string.OrdersActivity)))
        {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("fromNotification", getApplication().getString(R.string.orderFragment));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Orders Notification", NotificationManager.IMPORTANCE_DEFAULT);

            //Configure Notification Channel
            notificationChannel.setDescription("Orders Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_bell)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.DEFAULT_ALL);
        notificationManager.notify(0, notificationBuilder.build());
    }


}
