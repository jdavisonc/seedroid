package com.superdownloader.droideasy.webservices;

import android.content.Context;
import android.content.SharedPreferences;

import com.superdownloader.droideasy.tools.Prefs;

public class SuperdownloaderWSFactory {

	private static SuperdownloaderWSClient client = null;

	public static SuperdownloaderWSClient getClient(Context context) {
		if (client == null) {
			SharedPreferences sharedPrefs = Prefs.get(context);

			String username = sharedPrefs.getString("proeasy_username", "");
			String password = sharedPrefs.getString("proeasy_password", "");
			String serverUrl = sharedPrefs.getString("proeasy_server", "");

			// Initialize
			client = new SuperdownloaderWSClientImpl(username, password, serverUrl);
		}
		return client;
	}

}
