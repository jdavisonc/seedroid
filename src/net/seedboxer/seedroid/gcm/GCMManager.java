/*******************************************************************************
 * C2DMManager.java
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
package net.seedboxer.seedroid.gcm;

import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.tools.Prefs;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

/**
 * @author harley
 *
 */
public class GCMManager {

	private static final String TAG = "GCMManager";
	public static final String STATUS_EXTRA = "Status";
	public static final int REGISTERED_STATUS = 1;
	public static final int AUTH_ERROR_STATUS = 2;
	public static final int UNREGISTERED_STATUS = 3;
	public static final int ERROR_STATUS = 4;

	public static void clear(Context context) {
		GCMRegistrar.onDestroy(context);
	}
	
	public static void verifyRegistration(Context context) {		
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId == null || regId.equals("")) {
        	
        	String projectId = Prefs.get(context).getString(Prefs.PROJECT_ID, "");;
			if (projectId != null && !projectId.isEmpty()) {
	        	Log.i(TAG, "No existing registrationId. Registering...");
	            GCMRegistrar.register(context, projectId);
			}
        } else {
            // Device is already registered on GCM, check server.
        	Log.i(TAG, "Already registered. registrationId is " + regId);
            if (!GCMRegistrar.isRegisteredOnServer(context)) {
            	registerWithServer(context, regId);
            }
        }
		
	}

	public static boolean isRegistered(Context context) {
		String registrationId = GCMRegistrar.getRegistrationId(context);
		return registrationId != null && !"".equals(registrationId);
	}

	public static void registerWithServer(final Context context,
			final String regId) {
		Log.i(TAG, "Registered and got key: " + regId);
        final AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
            	// TODO: Add while with exponential backoff for when the server is offline or was a network issue
				try {
					SeedBoxerWSClient client = SeedBoxerWSFactory.getClient(context);
					boolean success = client.registerDevice(regId);
					if (success) {
						GCMRegistrar.setRegisteredOnServer(context, true);
					} else {
						Log.w(TAG, "Registration error: Wrong answer from server");
					}
				} catch (Exception e) {
					Log.w(TAG, "Registration error " + e.getMessage());
				}
				return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            }

        };
        mRegisterTask.execute(null, null, null);
	}

	public static void unregisterWithServer(final Context context,
			final String regId) {
        final AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
            	// TODO: Add while with exponential backoff for when the server is offline or was a network issue
				try {
					SeedBoxerWSClient client = SeedBoxerWSFactory.getClient(context);
					boolean success = client.unregisterDevice(regId);
					if (success) {
						GCMRegistrar.setRegisteredOnServer(context, false);
					} else {
						Log.w(TAG, "Unregistration error: Wrong answer from server");
					}
				} catch (Exception e) {
					Log.w(TAG, "Unregistration error " + e.getMessage());
				}
				return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            }

        };
        mRegisterTask.execute(null, null, null);
	}


}
