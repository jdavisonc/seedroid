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

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seedboxer.seedroid.tools.LauncherUtils;
import com.seedboxer.seedroid.types.Item;
import com.seedboxer.seedroid.ws.SeedBoxerWSClient;
import com.seedboxer.seedroid.ws.SeedBoxerWSFactory;

public class StatusActivity extends Activity {

	private ItemsAdapter statusItemsAdapter;

	private InQueueItemsAdapter inQueueItemsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


		setContentView(R.layout.status);

		// Set adapter
		ListView statusList = (ListView) findViewById(R.id.status_list);
		statusItemsAdapter = new ItemsAdapter(StatusActivity.this, R.layout.status_row, new ArrayList<Item>());
		statusList.setAdapter(statusItemsAdapter);

		ListView inQueueList = (ListView) findViewById(R.id.in_queue_list);
		inQueueItemsAdapter = new InQueueItemsAdapter(StatusActivity.this, R.layout.queue_row, new ArrayList<Item>());
		inQueueList.setAdapter(inQueueItemsAdapter);

		processLists();
	}

	private void processLists() {
		startProgressBar();
		new Thread(new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(StatusActivity.this);
					final List<Item> statusItems = wsclient.getStatusOfDownloads();
					final List<Item> inQueueItems = wsclient.getQueue();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderList(statusItemsAdapter, statusItems);
							renderList(inQueueItemsAdapter, inQueueItems);
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
	@TargetApi(11)
	private void renderList(ArrayAdapter<Item> adapter, List<Item> items) {
		if (items != null) {
			adapter.clear();
			adapter.addAll(items);
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

	/**
	 * Custom Adapter for render in queue item
	 * @author harley
	 *
	 */
	public class InQueueItemsAdapter extends ArrayAdapter<Item> {

		public InQueueItemsAdapter(Context context, int textViewResourceId, List<Item> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.queue_row, parent, false);
			}

			Item item = getItem(position);
			TextView label = (TextView) row.findViewById(R.id.name);
			label.setText(item.getName());
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
			processLists();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
