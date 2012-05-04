package com.superdownloader.droideasy.c2dm;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.c2dm.C2DMessaging;
import com.superdownloader.droideasy.tools.Prefs;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

/**
 * @author harley
 *
 */
public class C2DMManager {

	public static final String STATUS_EXTRA = "Status";
	public static final int REGISTERED_STATUS = 1;
	public static final int AUTH_ERROR_STATUS = 2;
	public static final int UNREGISTERED_STATUS = 3;
	public static final int ERROR_STATUS = 4;
	private static final String APP_C2DM_ID = "superdownloaderserver@gmail.com";

	public static void verifyRegistration(Context context) {
		String registrationId = C2DMessaging.getRegistrationId(context);
		if(registrationId != null && !"".equals(registrationId)){
			Log.i("GenericNotifier", "Already registered. registrationId is " + registrationId);
		}else{
			Log.i("GenericNotifier", "No existing registrationId. Registering..");
			C2DMessaging.register(context, APP_C2DM_ID);
		}
	}

	public static void registerWithServer(final Context context,
			final String deviceRegistrationID) {
		Log.i("C2DMManager", "Registered and got key: " + deviceRegistrationID);
		new Thread(new Runnable() {
			public void run() {

				// TODO: Add while with exponential backoff for when the server is offline or was a network issue
				try {
					String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
					SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(context);
					boolean success = client.registerDevice(deviceId, deviceRegistrationID);
					if (success) {
						SharedPreferences settings = Prefs.get(context);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("deviceRegistrationID", deviceRegistrationID);
						editor.commit();
					} else {
						Log.w("C2DMManager", "Registration error: Wrong answer from server");
					}
				} catch (Exception e) {
					Log.w("C2DMManager", "Registration error " + e.getMessage());
				}
			}
		}).start();
	}

	public static void unregisterWithServer(final Context context,
			final String deviceRegistrationID) {
		new Thread(new Runnable() {
			public void run() {

				// TODO: Add while with exponential backoff for when the server is offline or was a network issue
				try {
					String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
					SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(context);
					client.unregisterDevice(deviceId, deviceRegistrationID);
				} catch (Exception e) {
					Log.w("C2DMManager", "Unregistration error " + e.getMessage());
				} finally {
					SharedPreferences settings = Prefs.get(context);
					SharedPreferences.Editor editor = settings.edit();
					editor.remove("deviceRegistrationID");
					editor.remove("accountName");
					editor.commit();
				}
			}
		}).start();
	}


}
