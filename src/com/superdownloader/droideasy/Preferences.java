package com.superdownloader.droideasy;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

import com.superdownloader.droideasy.c2dm.C2DMManager;
import com.superdownloader.droideasy.tools.Prefs;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Prefs.get(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

		addPreferencesFromResource(R.xml.preferences);
		setContentView(R.layout.preferences_layout);
	}

	public void registerC2DMHandler(View view) {
		C2DMManager.verifyRegistration(this);
	}

	OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			SuperdownloaderWSFactory.changePreferences();
		}
	};


}
