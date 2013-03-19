/*******************************************************************************
 * Runners.java
 * 
 * Copyright (c) 2012 SeedBoxer Team.
 * 
 * This file is part of SeedBoxer.
 * 
 * SeedBoxer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SeedBoxer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SeedBoxer.  If not, see <http ://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.seedboxer.seedroid.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class Runners {

	public static void runOnThread(final Activity activity, final Runnable runnable) {
		final ProgressDialog dialog = startProgressBar(activity);
        final AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
				try {
					runnable.run();
				} finally {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							stopProgressBar(dialog);
						}
					});
				}
				return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            }

        };
        mRegisterTask.execute(null, null, null);
	}

	private static ProgressDialog startProgressBar(Activity activity) {
		//activity.setProgressBarIndeterminateVisibility(true);
		return ProgressDialog.show(activity, "", "Loading...");
	}

	private static void stopProgressBar(ProgressDialog dialog) {
		//activity.setProgressBarIndeterminateVisibility(false);
		dialog.dismiss();
	}
	
}
