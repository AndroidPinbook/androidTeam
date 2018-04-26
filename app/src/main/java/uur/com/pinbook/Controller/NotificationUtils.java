package uur.com.pinbook.Controller;


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.DebugUtils;
import android.widget.Toast;

import uur.com.pinbook.R;

public class NotificationUtils {

    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_1 = "action_1";
    Context context;

    public void displayNotification(Context context) {

        this.context = context;

        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.approve_icon)
                        .setContentTitle("Sample Notification")
                        .setContentText("Notification text goes here")
                        .addAction(new NotificationCompat.Action(R.drawable.batman_icon,
                                "Action 1", action1PendingIntent));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            //DebugUtils.log("Received notification action: " + action);
            if (ACTION_1.equals(action)) {
                Toast.makeText(context, "Action clicked !!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}