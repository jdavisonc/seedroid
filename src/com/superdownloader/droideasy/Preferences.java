package com.superdownloader.droideasy;

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

import com.superdownloader.droideasy.c2dm.C2DMManager;
import com.superdownloader.droideasy.tools.Prefs;
import com.superdownloader.droideasy.ws.SeedBoxerWSFactory;

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
		C2DMManager.verifyRegistration(this);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			;
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
