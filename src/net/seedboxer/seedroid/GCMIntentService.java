/*******************************************************************************
 * GCMIntentService.java
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
package net.seedboxer.seedroid;

import net.seedboxer.seedroid.gcm.GCMManager;
import net.seedboxer.seedroid.tools.LauncherUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String SUCCESS_RESPONSE = "OK";
	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super();
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        GCMManager.registerWithServer(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
		GCMManager.unregisterWithServer(context, registrationId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {     
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String success = extras.getString("success");
			String file = extras.getString("file");
			Log.d(TAG, "Success: " + success + ", file: " + file);
			if (success != null && SUCCESS_RESPONSE.equals(success)) {
				LauncherUtils.generateNotification(context, R.string.push_success, file);
			} else if (success != null) {
				LauncherUtils.generateNotification(context, R.string.push_fail, file);
			}
		}
        
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

}
