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
package net.seedboxer.seedroid.ws;

import net.seedboxer.seedroid.tools.Prefs;
import android.content.Context;
import android.content.SharedPreferences;


public class SeedBoxerWSFactory {

	private static SeedBoxerWSClient client = null;

	public static SeedBoxerWSClient getClient(Context context) {
		if (client == null) {
			SharedPreferences sharedPrefs = Prefs.get(context);

			String apikey = sharedPrefs.getString(Prefs.APIKEY, "");
			String serverUrl = sharedPrefs.getString(Prefs.SERVER, "");

			// Initialize
			client = new SeedBoxerWSClientImpl(apikey, serverUrl);
		}
		return client;
	}

	public static void changePreferences() {
		client = null;
	}

}
