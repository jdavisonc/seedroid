/*******************************************************************************
 * DownloadsFragment.java
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

import java.util.ArrayList;
import java.util.List;

import net.seedboxer.seedroid.R;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSClient;
import net.seedboxer.seedroid.services.seedboxer.SeedBoxerWSFactory;
import net.seedboxer.seedroid.services.seedboxer.types.FileValue;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.utils.Runners;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;


/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class DownloadsFragment extends ListFragment implements OnItemClickListener {
	
	private ArrayAdapter<FileValue> adapter;
	private ActionMode mActionMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);

		// Set adapter
		adapter = new ArrayAdapter<FileValue>(getActivity(), R.layout.downloads_row_view, new ArrayList<FileValue>());
		setListAdapter(adapter);
		
		setHasOptionsMenu(true);
		
		return root;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser == true) { 
			refresh();
		} else {
	    	if (mActionMode != null) {
	    		mActionMode.finish();
	    	}
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mActionMode = null;
		getListView().setItemsCanFocus(false);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		getListView().setOnItemClickListener(this);
	}

	private void refresh() {
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient wsclient = SeedBoxerWSFactory.getClient(getActivity());
					final List<FileValue> items = wsclient.getItemsAvaibleForDownload();

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
						}
					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}
	
	/**
	 * Method for add items to adapter and render it
	 * @param items
	 */
	private void renderItems(List<FileValue> items) {
		if (items != null) {
			adapter.clear();
			adapter.addAll(items);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.downloads_menu, menu);
		createFilteredMenu(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	/**
	 * Get the SearchView and set the searchable configuration
	 * @param menu
	 */
	private void createFilteredMenu(Menu menu) {
		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
	

	protected void download(final List<FileValue> toDownload) {
		Runners.runOnThread(getActivity(), new Runnable() {
			public void run() {
				try {
					SeedBoxerWSClient client = SeedBoxerWSFactory.getClient(getActivity());
					if (client.putToDownload(toDownload)) {
						LauncherUtils.showToast("Downloads are in the queue now!", getActivity());
					} else {
						LauncherUtils.showError("There was a problem when enqueuing downloads.", getActivity());
					}
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
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
				mActionMode = getView().startActionMode(new ModeCallback());
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
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.downloads_item_menu, menu);
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
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
					List<FileValue> toDownload = new ArrayList<FileValue>();
					for (int i = 0; i < selected.size(); i++) {
						if (selected.valueAt(i)) {
							FileValue it = (FileValue) getListView().getItemAtPosition(selected.keyAt(i));
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
