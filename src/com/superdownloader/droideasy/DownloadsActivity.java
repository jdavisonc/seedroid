package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.superdownloader.droideasy.tools.LauncherUtils;
import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class DownloadsActivity extends ListActivity implements OnItemClickListener {

	private ArrayAdapter<Item> adapter;
	private ActionMode mActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// Set adapter
		adapter = new ArrayAdapter<Item>(this, R.layout.rowbuttonlayout, new ArrayList<Item>());
		setListAdapter(adapter);

		mActionMode = null;
		getListView().setItemsCanFocus(false);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemClickListener(this);

		getAndFillDownloadsList();
		setHomeIcon();
	}

	private void setHomeIcon() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void getAndFillDownloadsList() {
		startProgressBar();
		new Thread(new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					SuperdownloaderWSClient wsclient = SuperdownloaderWSFactory.getClient(DownloadsActivity.this);
					final List<Item> items = wsclient.getItemsAvaibleForDownload();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
						}
					});
				} catch (Exception e) {
					Log.e("droidEasy", "Error communicating with proEasy.");
					LauncherUtils.showError("Error communicating with proEasy.", DownloadsActivity.this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.downloads_menu, menu);

		createFilteredMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Get the SearchView and set the searchable configuration
	 * @param menu
	 */
	private void createFilteredMenu(Menu menu) {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			public boolean onQueryTextSubmit(String query) {
				adapter.getFilter().filter(query);
				return true;
			}

			public boolean onQueryTextChange(String newText) {
				adapter.getFilter().filter(newText);
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_preferences:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, StatusActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Method for add items to adapter and render it
	 * @param items
	 */
	private void renderItems(List<Item> items) {
		if (items != null) {
			for (Item item : items) {
				adapter.add(item);
			}
			adapter.notifyDataSetChanged();
		}
	}

	protected void download(final List<Item> toDownload) {
		setProgressBarIndeterminateVisibility(true);
		new Thread(new Runnable() {
			public void run() {
				try {
					SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(DownloadsActivity.this);
					client.putToDownload(toDownload);

				} catch (Exception e) {
					Log.e("droidEasy", "Error communicating with proEasy.");
					LauncherUtils.showError("Error communicating with proEasy.", DownloadsActivity.this);
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

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Notice how the ListView api is lame
		// You can use mListView.getCheckedItemIds() if the adapter
		// has stable ids, e.g you're using a CursorAdaptor
		SparseBooleanArray checked = getListView().getCheckedItemPositions();
		boolean hasCheckedElement = false;
		for (int i = 0 ; i < checked.size() && ! hasCheckedElement ; i++) {
			hasCheckedElement = checked.valueAt(i);
		}

		if (hasCheckedElement) {
			if (mActionMode == null) {
				mActionMode = startActionMode(new ModeCallback());
			}
		} else {
			if (mActionMode != null) {
				mActionMode.finish();
			}
		}
	};

	private final class ModeCallback implements ActionMode.Callback {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Create the menu from the xml file
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.rowselection, menu);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Here, you can checked selected items to adapt available actions
			return false;
		}

		public void onDestroyActionMode(ActionMode mode) {
			// Destroying action mode, let's unselect all items
			for (int i = 0; i < getListView().getAdapter().getCount(); i++)
				getListView().setItemChecked(i, false);

			if (mode == mActionMode) {
				mActionMode = null;
			}
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			switch (item.getItemId()) {
			case R.id.menu_download:
				SparseBooleanArray selected = getListView().getCheckedItemPositions();
				if (selected != null && selected.size() > 0) {
					List<Item> toDownload = new ArrayList<Item>();
					for (int i = 0; i < selected.size(); i++) {
						if (selected.valueAt(i)) {
							Item it = (Item) getListView().getItemAtPosition(selected.keyAt(i));
							toDownload.add(it);
						}
					}
					download(toDownload);
				}
				mode.finish();
			default:
				mode.finish();
			}

			return true;
		}
	}

}