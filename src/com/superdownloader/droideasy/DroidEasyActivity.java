package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.superdownloader.droideasy.tools.LauncherUtils;
import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class DroidEasyActivity extends ListActivity implements OnItemClickListener {

	private ArrayAdapter<Item> adapter;
	private ProgressDialog m_ProgressDialog = null;
	private ActionMode mActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set adapter
		adapter = new ArrayAdapter<Item>(this, R.layout.rowbuttonlayout, new ArrayList<Item>());
		setListAdapter(adapter);

		mActionMode = null;
		getListView().setItemsCanFocus(false);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemClickListener(this);

		m_ProgressDialog = ProgressDialog.show(DroidEasyActivity.this, "Please wait...", "Retrieving data ...", true);

		// Create thread for get data
		new Thread(new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					SuperdownloaderWSClient wsclient = SuperdownloaderWSFactory.getClient(DroidEasyActivity.this);
					final List<Item> items = wsclient.getItemsAvaibleForDownload();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
							m_ProgressDialog.dismiss();
						}
					});
				} catch (Exception e) {
					Log.e("droidEasy", e.getMessage());
					m_ProgressDialog.dismiss();
					LauncherUtils.showError("Error communicating with proEasy.", DroidEasyActivity.this);
				}
			}
		}, "MagentoBackground").start();
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

	protected void download(List<Item> toDownload) {
		try {
			SuperdownloaderWSClient client = SuperdownloaderWSFactory.getClient(this);
			client.putToDownload(toDownload);
			// Show success action
		}catch (Exception e) {
			LauncherUtils.showError("Error at putting file to download.", this);
		}
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
				long[] selected = getListView().getCheckedItemIds();
				if (selected.length > 0) {
					List<Item> toDownload = new ArrayList<Item>();
					for (long id: selected) {
						Item it = (Item) getListView().getItemAtPosition((int)id);
						toDownload.add(it);
					}
					download(toDownload);
				}
				mode.finish();
			default:
				mode.finish();
			}

			return true;
		}
	};


}