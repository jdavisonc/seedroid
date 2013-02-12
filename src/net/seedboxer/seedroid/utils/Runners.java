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

/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class Runners {

	public static void runOnThread(final Activity activity, final Runnable runnable) {
		startProgressBar(activity);
		new Thread(new Runnable() {
			public void run() {
				try {
					runnable.run();
				} finally {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							stopProgressBar(activity);
						}
					});
				}
			}
		}, "MagentoBackground").start();
	}

	private static void startProgressBar(Activity activity) {
		activity.setProgressBarIndeterminateVisibility(true);
	}

	private static void stopProgressBar(Activity activity) {
		activity.setProgressBarIndeterminateVisibility(false);
	}
	
}
