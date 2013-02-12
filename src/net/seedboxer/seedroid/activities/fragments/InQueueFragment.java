/*******************************************************************************
 * InQueueFragment.java
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
import net.seedboxer.seedroid.services.seedboxer.types.Download;
import net.seedboxer.seedroid.services.seedboxer.types.FileValue;
import net.seedboxer.seedroid.tools.LauncherUtils;
import net.seedboxer.seedroid.utils.Runners;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * @author Jorge Davison (jdavisonc)
 *
 */
public class InQueueFragment extends ListFragment {
	
	private ArrayAdapter<FileValue> adapter;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View root = super.onCreateView(inflater, container, savedInstanceState);
        
		// Set adapter
        adapter = new ArrayAdapter<FileValue>(getActivity(), R.layout.queue_row, new ArrayList<FileValue>());
		setListAdapter(adapter);
		
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
					final List<FileValue> inQueueItems = wsclient.getQueue();

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							renderList(inQueueItems);
						}
					});
				} catch (Exception e) {
					Log.e("seedroid", "Error communicating with SeedBoxer.");
					LauncherUtils.showError("Error communicating with SeedBoxer.", getActivity());
				}
			}
		});
	}
	
	private void renderList(List<FileValue> items) {
		if (items != null) {
			for (FileValue item : items) {
				adapter.add(item);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	public class InQueueItemsAdapter extends ArrayAdapter<Download> {

		private final Bundle savedInstanceState;
		
		public InQueueItemsAdapter(Context context, Bundle savedInstanceState, int textViewResourceId, List<Download> items) {
			super(context, textViewResourceId, items);
			this.savedInstanceState = savedInstanceState;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater(savedInstanceState);
				row = inflater.inflate(R.layout.queue_row, parent, false);
			}

			Download item = getItem(position);
			TextView label = (TextView) row.findViewById(R.id.name);
			label.setText(item.getName());
			return row;
		}
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

}
