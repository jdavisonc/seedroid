/*******************************************************************************
 * Preferences.java
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
package com.seedboxer.seedroid;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.seedboxer.seedroid.c2dm.C2DMManager;
import com.seedboxer.seedroid.tools.LauncherUtils;
import com.seedboxer.seedroid.tools.Prefs;
import com.seedboxer.seedroid.ws.SeedBoxerWSFactory;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Prefs.get(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences_layout);

		showC2DMStatusText();
		setHomeIcon();
	}

	private void setHomeIcon() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void showC2DMStatusText() {
		TextView c2dmStatus = (TextView) findViewById(R.id.c2dm_status);
		if (C2DMManager.isRegistered(this)) {
			c2dmStatus.setText("Registered");
			c2dmStatus.setTextColor(Color.GREEN);
		} else {
			c2dmStatus.setText("NOT Registered");
			c2dmStatus.setTextColor(Color.RED);
		}
	}

	public void registerC2DMHandler(View view) {
		try {
			C2DMManager.verifyRegistration(this);
			Thread.sleep(100);
		} catch (InterruptedException e) {
			;
		} catch (IllegalArgumentException e) {
			LauncherUtils.showError(e.getMessage(), this);
		} finally {
			showC2DMStatusText();
		}
	}

	OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			SeedBoxerWSFactory.changePreferences();
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, StatusActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
