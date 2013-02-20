/*******************************************************************************
 * LoginActivity.java
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
import net.seedboxer.seedroid.R.id;
import net.seedboxer.seedroid.R.layout;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.tools.Prefs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class LoginActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.login);
	}
	
	public void handleLogin(View view) throws Exception {
		EditText username = (EditText) findViewById(R.id.username_edit);
		EditText password = (EditText) findViewById(R.id.password_edit);
		
		executeLogin(username.getText().toString(), password.getText().toString());
	}

	private void executeLogin(final String username, final String password) throws Exception {
		runOnThread(new Runnable() {
			public void run() {
				SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(LoginActivity.this);
				try {
					String apikey = wsclient.getApikey(username, password);
					Prefs.setString(LoginActivity.this, Prefs.APIKEY, apikey);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	private void runOnThread(final Runnable runnable) {
		final ProgressDialog dialog = startProgressBar();
		new Thread(new Runnable() {
			public void run() {
				try {
					runnable.run();
					runOnUiThread(new Runnable() {
						public void run() {
							stopProgressBar(dialog, true);
						}
					});
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							stopProgressBar(dialog, false);
						}
					});
				}
			}
		}, "MagentoBackground").start();
	}

	private ProgressDialog startProgressBar() {
		return ProgressDialog.show(this, "", "Logging...");
	}

	private void stopProgressBar(ProgressDialog dialog, boolean success) {
		dialog.dismiss();
		if (success) {
			setResult(RESULT_OK);
			finish();
		} else {
			LauncherUtils.showToast("Wrong username/password, please try again.", LoginActivity.this);
		}
	}
	
}
