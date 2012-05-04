/**
 * 
 */
package com.superdownloader.droideasy.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author harley
 *
 */
public final class Prefs {

	public static SharedPreferences get(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}
