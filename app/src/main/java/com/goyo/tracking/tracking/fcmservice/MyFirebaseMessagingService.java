package com.goyo.tracking.tracking.fcmservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.forms.vhtracking;
import com.goyo.tracking.tracking.main;

/**
 * Created by brittany on 4/3/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static String notifData;
    private String deduct_amount, paid_amount;
    String ride_id, mType = null, mTitle = null, mBody = null;
    public static final String MESSAGE_SUCCESS = "MessageSuccess";
    public static final String RIDE_CANCEL_BY_DRIVER = "RideCancelByDriver";
    public static final String COMPLETE_RIDE = "CompleteRide";
    public static final String MESAGE_ERROR = "MessageError";
    public static final String MESSAGE_NOTIFICATION = "MessageNotification";


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        //notifData= String.valueOf(remoteMessage);


        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "data: " + remoteMessage.getData());
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (remoteMessage.getFrom().startsWith("/topics/speed_")) {
                sendNotificationTrackStartTrip(remoteMessage.getNotification());

            }else if(remoteMessage.getFrom().startsWith("/topics/evt_")){
                sendNotificationTrackStartTrip(remoteMessage.getNotification());
                sendMessage();
            }
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//            } else {
//                // Handle message within 10 seconds
//
//            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");


    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("command_reply");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /*####################################Code By pratik (Don't touch)#######################################*/
    private void sendNotificationTrackStartTrip(RemoteMessage.Notification n) {
        try {
            Intent intent = new Intent(this, main.class);
            //intent.putExtra("tripid", n.getTitle());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_tone_2);
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_speed)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setContentTitle(n.getTitle())
                    .setContentText(n.getBody())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(n.getBody()))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(12 /* ID of notification */, notificationBuilder.build());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}



