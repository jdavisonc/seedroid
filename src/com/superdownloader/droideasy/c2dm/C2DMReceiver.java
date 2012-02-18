package com.superdownloader.droideasy.c2dm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;

import com.superdownloader.droideasy.R;

public class C2DMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.w("C2DM", "Message Receiver called");

		if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
			Log.w("C2DM", "Received message");
			final String payload = intent.getStringExtra("payload");
			Log.d("C2DM", "dmControl: payload = " + payload);
			// Send this to my application server
		} else if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			Log.w("C2DM", "Received registration ID");
			final String registrationId = intent.getStringExtra("registration_id");
			String error = intent.getStringExtra("error");

			String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			Log.d("C2DM", "dmControl: registrationId = " + registrationId + " deviceId = " + deviceId + ", error = " + error);

			createNotification(context, registrationId, deviceId);
			sendRegistrationIdToServer(deviceId, registrationId);
		}
	}

	private void sendRegistrationIdToServer(String deviceId, String registrationId) {
		// TODO Auto-generated method stub

	}

	private void createNotification(Context context, String registrationId, String deviceId) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.bittorrent, "Registration successful", System.currentTimeMillis());

		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		//Intent intent = new Intent(context, RegistrationResultActivity.class);
		//intent.putExtra("registration_id", registrationId);
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		//notification.setLatestEventInfo(context, "Registration", "Successfully registered", pendingIntent);
		notification.setLatestEventInfo(context, "Registration", "Successfully registered" + registrationId + " " + deviceId, null);
		notificationManager.notify(0, notification);
	}
}
