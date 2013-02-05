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
package net.seedboxer.seedroid.tools;

import net.seedboxer.seedroid.R;
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
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.bittorrent);
		builder.setContentTitle(title);
		builder.setContentText(message);
		builder.setWhen(System.currentTimeMillis());
		builder.setAutoCancel(true);

		SharedPreferences settings = Prefs.get(context);
		int notificatonID = settings.getInt("notificationID", 0);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificatonID, builder.getNotification());
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
