/*******************************************************************************
 * LauncherUtils.java
 * 
 * Copyright (c) 2012 SeedBoxer Team.
 * 
 * This file is part of Seedroid.
 * 
 * Seedroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Seedroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Seedroid.  If not, see <http ://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.seedboxer.seedroid.tools;

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

import com.seedboxer.seedroid.R;

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