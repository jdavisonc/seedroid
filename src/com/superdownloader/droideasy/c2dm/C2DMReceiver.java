package com.superdownloader.droideasy.c2dm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;

import com.superdownloader.droideasy.tools.GUIFunctions;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

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

			sendRegistrationIdToServer(context, deviceId, registrationId);
		}
	}

	private void sendRegistrationIdToServer(Context context, String deviceId, String registrationId) {
		GUIFunctions.createNotification(context, "Registration Successful","Successfully registered" + registrationId + " " + deviceId);
		try {
			SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(context);
			client.registerDevice(deviceId, registrationId);
		} catch (Exception e) {

		}
	}

}
