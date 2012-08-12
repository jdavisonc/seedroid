package com.superdownloader.droideasy.ws;

import android.content.Context;
import android.content.SharedPreferences;

import com.superdownloader.droideasy.tools.Prefs;

public class SeedBoxerWSFactory {

	private static SeedBoxerWSClient client = null;

	public static SeedBoxerWSClient getClient(Context context) {
		if (client == null) {
			SharedPreferences sharedPrefs = Prefs.get(context);

			String username = sharedPrefs.getString("proeasy_username", "");
			String password = sharedPrefs.getString("proeasy_password", "");
			String serverUrl = sharedPrefs.getString("proeasy_server", "");

			// Initialize
			client = new SeedBoxerWSClientImpl(username, password, serverUrl);
		}
		return client;
	}

	public static void changePreferences() {
		client = null;
	}

}
