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
package net.seedboxer.seedroid.activities;

import net.seedboxer.seedroid.R;
import net.seedboxer.seedroid.gcm.GCMManager;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.tools.Prefs;
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


public class Preferences extends PreferenceActivity {

	private static final int APIKEY_REQUEST = 0;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Prefs.get(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences_layout);

		showAuthStatusText();
		setHomeIcon();
	}

	private void setHomeIcon() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void showAuthStatusText() {
		TextView c2dmStatus = (TextView) findViewById(R.id.apikey_status);
		String apikey = Prefs.get(this).getString(Prefs.APIKEY, null);
		if (apikey != null) {
			c2dmStatus.setText(apikey);
			c2dmStatus.setTextColor(Color.GREEN);
		} else {
			c2dmStatus.setText("Missing ApiKey");
			c2dmStatus.setTextColor(Color.RED);
		}
	}

	public void registerGCMHandler() {
		try {
			GCMManager.retrieveProjectIdAndRegister(this);
		} catch (IllegalArgumentException e) {
			LauncherUtils.showError("Error in registration to receive notifications: " + e.getMessage(), this);
		}
	}
	
	public void getApikeyHandler(View view) {
		startActivityForResult(new Intent(this, LoginActivity.class), APIKEY_REQUEST);
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APIKEY_REQUEST) {
            if (resultCode == RESULT_OK) {
            	SeedBoxerWSFactory.changePreferences();
            	registerGCMHandler();
            	showAuthStatusText();
            }
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
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
