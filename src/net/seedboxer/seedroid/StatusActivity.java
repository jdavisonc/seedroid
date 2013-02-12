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
package net.seedboxer.seedroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.services.seedboxer.types.Download;
import net.seedboxer.seedroid.tools.LauncherUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatusActivity extends Activity {

	private ItemsAdapter statusItemsAdapter;

	private InQueueItemsAdapter inQueueItemsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// Set adapter
		//ListView statusList = (ListView) findViewById(R.id.status_list);
		statusItemsAdapter = new ItemsAdapter(StatusActivity.this, R.layout.status_row, new ArrayList<Download>());
		//statusList.setAdapter(statusItemsAdapter);

		ListView inQueueList = (ListView) findViewById(R.id.in_queue_list);
		inQueueItemsAdapter = new InQueueItemsAdapter(StatusActivity.this, R.layout.queue_row, new ArrayList<Download>());
		inQueueList.setAdapter(inQueueItemsAdapter);
		registerForContextMenu(inQueueList);
		
		processLists();
	}

	private void runOnThread(final Runnable runnable) {
		startProgressBar();
		new Thread(new Runnable() {
			public void run() {
				try {
					runnable.run();
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

	private void processLists() {
		runOnThread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(StatusActivity.this);
					//Download statusOfDownloads = wsclient.getStatusOfDownloads();
					final List<Download> statusItems = Collections.emptyList();
					//final List<Download> inQueueItems = wsclient.getQueue();

					runOnUiThread(new Runnable() {
						public void run() {
							renderList(statusItemsAdapter, statusItems);
							//renderList(inQueueItemsAdapter, inQueueItems);
						}
					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", StatusActivity.this);
				}
			}
		});
	}

	private void startProgressBar() {
		setProgressBarIndeterminateVisibility(true);
	}

	private void stopProgressBar() {
		setProgressBarIndeterminateVisibility(false);
	}

	private void renderList(ArrayAdapter<Download> adapter, List<Download> items) {
		if (items != null) {
			adapter.clear();
			adapter.addAll(items);
			adapter.notifyDataSetChanged();
		}
	}

	public class ItemsAdapter extends ArrayAdapter<Download> {

		public ItemsAdapter(Context context, int textViewResourceId, List<Download> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.status_row, parent, false);
			}

			Download item = getItem(position);
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

	public class InQueueItemsAdapter extends ArrayAdapter<Download> {

		public InQueueItemsAdapter(Context context, int textViewResourceId, List<Download> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.queue_row, parent, false);
			}

			Download item = getItem(position);
			TextView label = (TextView) row.findViewById(R.id.name);
			label.setText(item.getName());
			return row;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.in_queue_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_delete:
			deleteInQueueItem(info.position);
			processLists();
			return true;
		default:
			return super.onContextItemSelected(item);
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
		case R.id.menu_refresh:
			processLists();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void deleteInQueueItem(final int position) {
		runOnThread(new Runnable() {
			public void run() {
				try {
					ListView inQueueList = (ListView) findViewById(R.id.in_queue_list);
					final Download itemToRemove = (Download) inQueueList.getAdapter().getItem(position);

					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(StatusActivity.this);
					/*if (wsclient.removeFromQueue(itemToRemove.getQueueId())) {
						LauncherUtils.showToast("Item remove from queue.", StatusActivity.this);
					} else {
						LauncherUtils.showToast("Error removing item from queue.", StatusActivity.this);
					}*/
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", StatusActivity.this);
				}
			}
		});
	}

}
