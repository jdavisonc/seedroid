package com.superdownloader.droideasy.tools;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;

import com.superdownloader.droideasy.R;

public class GUIFunctions {

	public static void showError(String error, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(error);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void createNotification(Context context, String title, String message) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.bittorrent, title, System.currentTimeMillis());

		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Intent intent = new Intent(context, RegistrationResultActivity.class);
		//intent.putExtra("registration_id", registrationId);
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		//notification.setLatestEventInfo(context, "Registration", "Successfully registered", pendingIntent);
		notification.setLatestEventInfo(context, title, message, null);
		notificationManager.notify(0, notification);
	}

}
