package es.tfg.service;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.tfg.R;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notify(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    public void notify(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notif_channel")
                .setSmallIcon(R.drawable.ic_games)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(123, builder.build());
    }
}
