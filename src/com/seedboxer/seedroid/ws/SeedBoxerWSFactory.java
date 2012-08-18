/*******************************************************************************
 * SeedBoxerWSFactory.java
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
package com.seedboxer.seedroid.ws;

import android.content.Context;
import android.content.SharedPreferences;

import com.seedboxer.seedroid.tools.Prefs;

public class SeedBoxerWSFactory {

	private static SeedBoxerWSClient client = null;

	public static SeedBoxerWSClient getClient(Context context) {
		if (client == null) {
			SharedPreferences sharedPrefs = Prefs.get(context);

			String username = sharedPrefs.getString("seedroid_username", "");
			String password = sharedPrefs.getString("seedroid_password", "");
			String serverUrl = sharedPrefs.getString("seedroid_server", "");

			// Initialize
			client = new SeedBoxerWSClientImpl(username, password, serverUrl);
		}
		return client;
	}

	public static void changePreferences() {
		client = null;
	}

}
