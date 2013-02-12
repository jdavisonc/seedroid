/*******************************************************************************
 * StatusFragment.java
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
package net.seedboxer.seedroid.activities.fragments;

import net.seedboxer.seedroid.R;
import net.seedboxer.seedroid.StatusActivity;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.services.seedboxer.types.Download;
import net.seedboxer.seedroid.services.seedboxer.types.UserStatusAPIResponse;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.utils.Runners;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class StatusFragment extends Fragment {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.status_view, container, false);
        setHasOptionsMenu(true);
        return root;
    }
    
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
    	super.onViewStateRestored(savedInstanceState);
    	process();
    }
    
	private void process() {
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(getActivity());
					final UserStatusAPIResponse userStatus = wsclient.getStatusOfDownloads();

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							render(userStatus);
						}

					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}
    
	private void render(UserStatusAPIResponse userStatus) {
		Download item = userStatus.getDownload();
		if (item != null) {
			renderDownload(item);
		} else {
			renderNothing();
		}
	}

	private void renderNothing() {
		View layoutNoDownloads = getView().findViewById(R.id.relative_layout_no_download);
		layoutNoDownloads.setVisibility(LinearLayout.VISIBLE);
		View layout = getView().findViewById(R.id.relative_layout);
		layout.setVisibility(LinearLayout.GONE);
	}


	private void renderDownload(Download item) {
		TextView label = (TextView) getView().findViewById(R.id.name);
		label.setText(item.getName());
		
		ImageView icon = (ImageView) getView().findViewById(R.id.icon);
		if (item.getName().matches("S\\d\\dE\\d\\d")) {
			icon.setImageResource(R.drawable.tvshow_icon);
		} else {
			icon.setImageResource(R.drawable.movie_icon);
		}

		double progress = calculateProgress(item);
		ProgressBar bar = (ProgressBar) getView().findViewById(R.id.progressbar);
		bar.setProgress((int) progress);

		TextView percentage = (TextView) getView().findViewById(R.id.percentage);
		percentage.setText(round(progress, 1) + "%");
		
		View layout = getView().findViewById(R.id.relative_layout);
		layout.setVisibility(LinearLayout.VISIBLE);
		View layoutNoDownloads = getView().findViewById(R.id.relative_layout_no_download);
		layoutNoDownloads.setVisibility(LinearLayout.GONE);
	}
    
	private double calculateProgress(Download item) {
		return (item.getSize()==0) ? 0 : (double) ((double)item.getTransferred() * 100 / item.getSize());
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			process();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/*private void deleteInQueueItem(final int position) {
		runOnThread(new Runnable() {
			public void run() {
				try {
					ListView inQueueList = (ListView) findViewById(R.id.in_queue_list);
					final Download itemToRemove = (Download) inQueueList.getAdapter().getItem(position);

					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(StatusActivity.this);
					if (wsclient.removeFromQueue(itemToRemove.getQueueId())) {
						LauncherUtils.showToast("Item remove from queue.", StatusActivity.this);
					} else {
						LauncherUtils.showToast("Error removing item from queue.", StatusActivity.this);
					}
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", StatusActivity.this);
				}
			}
		});
	}*/
	
}
