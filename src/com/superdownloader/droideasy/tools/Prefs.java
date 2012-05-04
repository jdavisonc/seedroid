/**
 * 
 */
package com.superdownloader.droideasy.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author harley
 *
 */
public final class Prefs {

	public static SharedPreferences get(Context context) {
		//return PreferenceManager.getDefaultSharedPreferences(context);
		return context.getSharedPreferences("hola", 0);
	}

}
