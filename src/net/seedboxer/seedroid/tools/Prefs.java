/*******************************************************************************
 * Prefs.java
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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author harley
 *
 */
public final class Prefs {

	public static final String PROJECT_ID = "seedroid_project_id";
	public static final String SERVER = "seedroid_server";
	public static final String APIKEY = "seedroid_apikey";

	public static SharedPreferences get(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static void setString(Context context, String key, String value) {
		Editor editor = get(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

}
