/*******************************************************************************
 * StatusActivity.java
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

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seedboxer.seedroid.tools.LauncherUtils;
import com.seedboxer.seedroid.types.Item;
import com.seedboxer.seedroid.ws.SeedBoxerWSClient;
import com.seedboxer.seedroid.ws.SeedBoxerWSFactory;

public class StatusActivity extends ListActivity {

	private ItemsAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


		setContentView(R.layout.status);

		// Set adapter
		adapter = new ItemsAdapter(StatusActivity.this, R.layout.status_row, new ArrayList<Item>());
		setListAdapter(adapter);

		processStatusList();
	}

	private void processStatusList() {
		startProgressBar();
		new Thread(new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(StatusActivity.this);
					final List<Item> items = wsclient.getStatusOfDownloads();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
						}
					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", StatusActivity.this);
				} finally {
					runOnUiThread(new Runnable() {
						public void run() {
							stopProgressBar();
						}
					});
				}
			}
		}, "MagentoBackground").start();
	}

	private void startProgressBar() {
		setProgressBarIndeterminateVisibility(true);
	}

	private void stopProgressBar() {
		setProgressBarIndeterminateVisibility(false);
	}

	/**
	 * Method for add items to adapter and render it
	 * @param items
	 */
	private void renderItems(List<Item> items) {
		if (items != null) {
			adapter.clear();
			for (Item item : items) {
				adapter.add(item);
			}
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Custom Adapter for render item
	 * @author harley
	 *
	 */
	public class ItemsAdapter extends ArrayAdapter<Item> {

		public ItemsAdapter(Context context, int textViewResourceId, List<Item> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.status_row, parent, false);
			}

			Item item = getItem(position);
			TextView label = (TextView) row.findViewById(R.id.name);
			label.setText(item.getName());

			int progress = (int) (item.getTransferred() * 100 / item.getSize());
			ProgressBar bar = (ProgressBar) row.findViewById(R.id.progressbar);
			bar.setProgress(progress);

			TextView percentage = (TextView) row.findViewById(R.id.percentage);
			percentage.setText(progress + "%");

			return row;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.menu_downloads:
			startActivity(new Intent(this, DownloadsActivity.class));
			return true;
		case R.id.menu_refresh:
			processStatusList();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}