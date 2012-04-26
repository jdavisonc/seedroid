package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClientImpl;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class StatusActivity extends ListActivity {
	
	private static final String DEFAULT_SERVER_URL = "http://ks313077.kimsufi.com:8080/proEasy-1.0/webservices/";

	private ItemsAdapter adapter;
	private ProgressDialog m_ProgressDialog = null;
	private SuperdownloaderWSClient wsclient = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        // Initialize
        wsclient = SuperdownloaderWSFactory.getClient(this);

        // Set adapter
        adapter = new ItemsAdapter(StatusActivity.this, R.layout.status_row, new ArrayList<Item>());
		setListAdapter(adapter);

		// Create thread for get data
		Thread thread = new Thread(null, new Runnable() {
			public void run() {
				try {
					// Calling Web Service
					final List<Item> items = wsclient.getStatusOfDownloads();

					// Render Items
					runOnUiThread(new Runnable() {
						public void run() {
							renderItems(items);
							m_ProgressDialog.dismiss();
						}
					});
				} catch (Exception e) {
					Log.e("droidEasy", e.getMessage());
				}
			}
		}, "MagentoBackground");

		thread.start();
		m_ProgressDialog = ProgressDialog.show(StatusActivity.this, "Please wait...", "Retrieving data ...", true);
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
	        case R.id.menu_list:
	            startActivity(new Intent(this, DroidEasyActivity.class));
	            return true;
	        case R.id.menu_preferences:
	            startActivity(new Intent(this, Preferences.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}