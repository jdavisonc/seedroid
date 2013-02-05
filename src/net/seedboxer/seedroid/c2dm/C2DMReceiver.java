/*******************************************************************************
 * C2DMReceiver.java
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
package net.seedboxer.seedroid.c2dm;

import java.io.IOException;

import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.tools.Prefs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {

	public C2DMReceiver() {
		super("senderemail@gmail.com");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String payload = extras.getString("payload");
			Log.d("C2DMReceiver", "Payload: " + payload);
			String[] values = payload.split(";");
			if ("OK".equals(values[0])) {
				LauncherUtils.generateNotification(context, "Upload completed", values[1]);
			} else {
				LauncherUtils.generateNotification(context, "Upload failed", values[1]);
			}
		}
	}

	@Override
	public void onError(Context context, String errorId) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onRegistered(Context context, String registrationId) throws IOException {
		C2DMManager.registerWithServer(context, registrationId);
	}

	@Override
	public void onUnregistered(Context context) {
		SharedPreferences prefs = Prefs.get(context);
		String deviceRegistrationID = prefs.getString("deviceRegistrationID", null);
		C2DMManager.unregisterWithServer(context, deviceRegistrationID);
	}

}
