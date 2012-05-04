package com.superdownloader.droideasy.c2dm;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.superdownloader.droideasy.tools.LauncherUtils;
import com.superdownloader.droideasy.tools.Prefs;

public class C2DMReceiver extends C2DMBaseReceiver {

	public C2DMReceiver() {
		super("senderemail@gmail.com");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String payload = extras.getString("payload");
			Log.d("C2DMReceiver", "Payload: " + payload);
			String[] values = payload.split(";");
			if ("OK".equals(values[0])) {
				LauncherUtils.generateNotification(context, "Upload completed", values[1]);
			} else {
				LauncherUtils.generateNotification(context, "Upload failed", values[1]);
			}
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegistered(Context context, String registrationId) throws IOException {
		C2DMManager.registerWithServer(context, registrationId);
	}

	@Override
	public void onUnregistered(Context context) {
		SharedPreferences prefs = Prefs.get(context);
		String deviceRegistrationID = prefs.getString("deviceRegistrationID", null);
		C2DMManager.unregisterWithServer(context, deviceRegistrationID);
	}

}
