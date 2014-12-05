package com.example.pushnotifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String message = "";
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
		String messageType = googleCloudMessaging.getMessageType(intent);

		if (!extras.isEmpty() && messageType != null) {

			switch (messageType) {

			case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:

				message = "Send ERROR : " + extras.toString();

				break;

			case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:

				message = "Deleted messages from Server : " + extras.toString();

				break;

			case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:

				message = "Message received from Google GCM Server : \n\n" + extras.getString(Constants.KEY_MESSAGE);

				break;

			default:
				break;
			}

			sendNotification(message);
			GCMBroadcastReceiver.completeWakefulIntent(intent);
		}
	}

	/**
	 * Send notification from IntentService
	 * 
	 * @param message
	 */
	private void sendNotification(String message) {

		if (message != null) {

			Intent intent = new Intent(this, ResultActivity.class);
			intent.putExtra(Constants.KEY_MESSAGE, message);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

			builder.setContentTitle("Message").setContentText("You've received a new message from Server!")
					.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true).setContentIntent(pendingIntent);

			notificationManager.notify(1, builder.build());
		}
	}

}
