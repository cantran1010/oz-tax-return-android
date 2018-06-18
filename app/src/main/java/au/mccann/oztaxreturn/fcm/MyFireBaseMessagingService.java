/*
  Copyright 2016 Google Inc. All Rights Reserved.
  <p/>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p/>
  http://www.apache.org/licenses/LICENSE-2.0
  <p/>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package au.mccann.oztaxreturn.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import au.mccann.oztaxreturn.R;
import au.mccann.oztaxreturn.activity.SplashActivity;
import au.mccann.oztaxreturn.common.Constants;
import au.mccann.oztaxreturn.database.UserManager;
import au.mccann.oztaxreturn.model.Notification;
import au.mccann.oztaxreturn.utils.LogUtils;
import au.mccann.oztaxreturn.utils.Utils;


/**
 * When implement FCM, add project on fire base console, and change google-service.json
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFireBaseMessagingService.class.getName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Fire Base Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        LogUtils.d(TAG, "onMessageReceived start");
        LogUtils.d(TAG, "onMessageReceived Message Data : " + remoteMessage.getData());
        LogUtils.d(TAG, "onMessageReceived Message Data --> data : " + remoteMessage.getData().get("data"));

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Notification notification = gson.fromJson(remoteMessage.getData().get("data"), Notification.class);
        sendNotification(notification);

//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(intent);

    }

    private void sendNotification(Notification notification) {
        if (!UserManager.checkLogin()) return;
        if (notification == null || notification.getEvent() == null) return;
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.putExtra(Constants.NOTIFICATION_EXTRA,notification);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notification.getId() /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "OZ_TAX")
                .setContentTitle(getString(R.string.app_name_old))
                .setContentText(Utils.getContentNotification(getApplicationContext(), notification))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

        if(notification.getEvent().equals("message_received")){
            Intent intentPushCount = new Intent();
            intentPushCount.setAction(Constants.BROAD_CAST_PUSH_CHAT);
            sendBroadcast(intentPushCount);
        }

    }

}
