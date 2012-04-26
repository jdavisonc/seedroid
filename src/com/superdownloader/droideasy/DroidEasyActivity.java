package com.superdownloader.droideasy;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.PendingIntent;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.superdownloader.droideasy.types.Item;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClient;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSClientImpl;
import com.superdownloader.droideasy.webservices.SuperdownloaderWSFactory;

public class DroidEasyActivity extends ListActivity {

	private ItemsAdapter adapter;
	private ProgressDialog m_ProgressDialog = null;
	private SuperdownloaderWSClient wsclient = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerC2DM();
        
        // Initialize
        wsclient = SuperdownloaderWSFactory.getClient(this);

        // Set adapter
        adapter = new ItemsAdapter(DroidEasyActivity.this, R.layout.row, new ArrayList<Item>());
		setListAdapter(adapter);

		// Create thread for get data
		Thread thread = new Thread(null, new Runnable() {
			public void run() {
				try {
					// Calling Web Service
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
				}
			}
		}, "MagentoBackground");

		thread.start();
		m_ProgressDialog = ProgressDialog.show(DroidEasyActivity.this, "Please wait...", "Retrieving data ...", true);
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
				row = inflater.inflate(R.layout.row, parent, false);
			}

			TextView label = (TextView) row.findViewById(R.id.weekofday);
			label.setText(getItem(position).getName());

			return row;
		}
	}

	private void registerC2DM() {
		Log.w("C2DM", "start registration process");
		Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
		intent.putExtra("app",PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		intent.putExtra("sender", "jdavisonc@gmail.com");
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

}