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
			handleMessage(context, intent);
		} else if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
			handleRegistration(context, intent);
		}
	}

	private void handleMessage(Context context, Intent intent) {
		String payload = intent.getStringExtra("payload");
		Log.d("C2DM", "dmControl: payload = " + payload);
		String[] values = payload.split(";");
		if ("OK".equals(values[0])) {
			GUIFunctions.createNotification(context, "Upload completed", values[1]);
		} else {
			GUIFunctions.createNotification(context, "Upload failed", values[1]);
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		String registrationId = intent.getStringExtra("registration_id");
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		Log.d("C2DM", "dmControl: registrationId = " + registrationId + " deviceId = " + deviceId );
		try {
			SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(context);
			client.registerDevice(deviceId, registrationId);
			GUIFunctions.createNotification(context, "Registration Successful","Successfully registered" + registrationId + " " + deviceId);
		} catch (Exception e) {
			GUIFunctions.createNotification(context, "Registration failed","");
		}
	}

}
