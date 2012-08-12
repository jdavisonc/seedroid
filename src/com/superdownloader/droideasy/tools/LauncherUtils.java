package com.superdownloader.droideasy.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.superdownloader.droideasy.R;

public class LauncherUtils {

	public static void showError(final String error, final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage(error);
				builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	public static void showToast(final String notification, final Activity activity) {
		final int duration = Toast.LENGTH_SHORT;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity, notification, duration).show();
			}
		});
	}

	public static void generateNotification(Context context, String title, String message) {
		int icon = R.drawable.bittorrent;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, title, when);

		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Intent intent = new Intent(context, RegistrationResultActivity.class);
		//intent.putExtra("registration_id", registrationId);
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		//notification.setLatestEventInfo(context, "Registration", "Successfully registered", pendingIntent);
		notification.setLatestEventInfo(context, title, message, null);

		SharedPreferences settings = Prefs.get(context);
		int notificatonID = settings.getInt("notificationID", 0);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificatonID, notification);
		playNotificationSound(context);

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("notificationID", ++notificatonID % 32);
		editor.commit();
	}

	public static void playNotificationSound(Context context) {
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (uri != null) {
			Ringtone rt = RingtoneManager.getRingtone(context, uri);
			if (rt != null) {
				rt.setStreamType(AudioManager.STREAM_NOTIFICATION);
				rt.play();
			}
		}
	}

}
