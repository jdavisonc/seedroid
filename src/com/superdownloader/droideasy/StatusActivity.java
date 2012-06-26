package com.superdownloader.droideasy;

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

import com.superdownloader.droideasy.tools.LauncherUtils;
import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

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
					SuperdownloaderWSClient wsclient = SuperdownloaderWSFactory.getClient(StatusActivity.this);
					final List<Item> items = wsclient.getStatusOfDownloads();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
						}
					});
				} catch (Exception e) {
					Log.e("droidEasy", "Error communicating with proEasy.");
					LauncherUtils.showError("Error communicating with proEasy.", StatusActivity.this);
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